package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheType;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.config.CacheConfigException;
import com.igeeksky.xcache.config.props.CacheProps;
import com.igeeksky.xcache.config.CacheConfigUtil;
import com.igeeksky.xcache.config.props.LocalProps;
import com.igeeksky.xcache.config.props.TemplateId;
import com.igeeksky.xcache.extension.compress.Compressor;
import com.igeeksky.xcache.extension.contains.AlwaysTrueContainsPredicate;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.contains.ContainsPredicateProvider;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.lock.CacheLockProvider;
import com.igeeksky.xcache.extension.lock.LocalCacheLock;
import com.igeeksky.xcache.extension.lock.LocalCacheLockProvider;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProvider;
import com.igeeksky.xcache.extension.serializer.Serializer;
import com.igeeksky.xcache.extension.statistic.CacheStatManager;
import com.igeeksky.xcache.extension.statistic.CacheStatMonitor;
import com.igeeksky.xcache.extension.statistic.LogCacheStatManager;
import com.igeeksky.xcache.extension.sync.*;
import com.igeeksky.xcache.store.LocalCacheStore;
import com.igeeksky.xcache.store.LocalCacheStoreProvider;
import com.igeeksky.xcache.store.RemoteCacheStoreProvider;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class XcacheManager implements CacheManager {

    private static final String SYNC_SEPARATOR = ":sync:";

    private final String sid = UUID.randomUUID().toString();

    private final ConcurrentMap<String, Cache<?, ?>> cacheMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheProps> cachePropsMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<TemplateId, CacheProps> templateMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheLockProvider> lockProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheSyncManager> syncProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheStatManager> statProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheMonitorProvider> monitorProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, LocalCacheStoreProvider> localStoreProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, RemoteCacheStoreProvider> remoteStoreProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ContainsPredicateProvider> predicateProviderMap = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getOrCreateCache(String name, Class<K> keyType, Class<V> valueType) {
        String cacheName = StringUtils.trim(name);
        if (!StringUtils.hasLength(cacheName)) {
            throw new CacheConfigException("cacheName must not be null or empty");
        }
        return (Cache<K, V>) cacheMap.computeIfAbsent(cacheName, k -> createCache(k, keyType, valueType));
    }

    private <K, V> Cache<K, V> createCache(String name, Class<K> keyType, Class<V> valueType) {
        // 1. 获取配置
        CacheProps cacheProps = this.getOrCreateCacheProps(name);

        // 2. 创建 CacheConfig
        CacheConfig<K, V> cacheConfig = CacheConfigUtil.createConfig(cacheProps, keyType, valueType);

        // 3. 设置 ContainsPredicate
        cacheConfig.setContainsPredicate(this.getContainsPredicate(keyType, cacheProps));

        // 4. 设置 CacheLock
        cacheConfig.setCacheLock(this.getCacheLock(keyType, cacheProps));

        // 5. 设置 CacheMonitors
        List<CacheMonitor<V>> monitors = this.getCacheMonitors(name, keyType, valueType, cacheProps);
        cacheConfig.setMonitors(monitors);

        // 6. 添加缓存统计类
        CacheStatMonitor<V> statMonitor = new CacheStatMonitor<>(name, cacheConfig.getApplication());
        monitors.add(statMonitor);
        CacheStatManager statManager = getCacheStatManager(cacheProps);
        statManager.register(name, statMonitor);

        // 7. 通过 CacheProps 判断是否需要创建 CacheStore
        CacheType cacheType = this.getCacheType(cacheProps);
        if (cacheType == CacheType.NONE) {
            return new NoOpCache<>(cacheConfig);
        }

        // 8. 添加缓存数据同步类
        String channel = getChannel(name, cacheProps);
        CacheSyncManager syncManager = getCacheSyncManager(cacheProps);
        Serializer<CacheSyncMessage> syncSerializer = getSyncMessageSerializer(cacheProps);
        CacheSyncMonitor<V> syncMonitor = getCacheSyncMonitor(channel, syncSerializer, syncManager, cacheType);
        if (syncMonitor != null) {
            monitors.add(syncMonitor);
        }

        // 9. 创建 KeyConvertor
        KeyConvertor keyConvertor = getKeyConvertor(cacheProps);
        cacheConfig.setKeyConvertor(keyConvertor);

        // 10. 创建本地缓存数据序列化器
        Serializer<V> localSerializer = getLocalSerializer(cacheProps);
        cacheConfig.getLocalConfig().setValueSerializer(localSerializer);

        // 11. 创建本地缓存数据压缩器
        Compressor localCompressor = getLocalCompressor(cacheProps);
        cacheConfig.getLocalConfig().setValueCompressor(localCompressor);

        if (cacheType == CacheType.LOCAL) {
            // 创建 LocalCacheStore
            LocalProps local = cacheProps.getLocal();
            String beanId = local.getCacheStore();
            LocalCacheStoreProvider localStoreProvider = localStoreProviderMap.get(beanId);
            // 构建本地缓存配置

            // 获取缓存
            LocalCacheStore localCacheStore = localStoreProvider.getLocalCacheStore(cacheConfig);
            registerSyncConsumer(syncManager, localCacheStore, syncSerializer, channel);

        }

        // 13. 创建远程缓存数据序列化器
        Serializer<V> remoteSerializer = getRemoteSerializer(cacheProps);
        cacheConfig.getRemoteConfig().setValueSerializer(remoteSerializer);

        // 14. 创建远程缓存数据压缩器
        Compressor remoteCompressor = getRemoteCompressor(cacheProps);
        cacheConfig.getRemoteConfig().setValueCompressor(remoteCompressor);

        if (cacheType == CacheType.REMOTE) {
            // 15. 创建并返回远程缓存
            String cacheStoreId = cacheProps.getRemote().getCacheStore();
            RemoteCacheStoreProvider cacheStoreProvider = remoteStoreProviderMap.get(cacheStoreId);
            cacheStoreProvider.getRemoteCacheStore(cacheConfig);

        }

        // 16. 创建并返回两级缓存

        return null;
    }

    private Compressor getRemoteCompressor(CacheProps cacheProps) {
        return null;
    }

    private <V> Serializer<V> getRemoteSerializer(CacheProps cacheProps) {
        return null;
    }

    private <V> Serializer<V> getLocalSerializer(CacheProps cacheProps) {
        return null;
    }

    private KeyConvertor getKeyConvertor(CacheProps cacheProps) {
        return null;
    }

    private Compressor getLocalCompressor(CacheProps cacheProps) {
        return null;
    }

    private void registerSyncConsumer(CacheSyncManager syncManager, LocalCacheStore localCacheStore, Serializer<CacheSyncMessage> syncSerializer, String channel) {
        if (syncManager != null) {
            CacheSyncConsumer syncConsumer = new CacheSyncConsumer(sid, localCacheStore, syncSerializer);
            syncManager.register(channel, syncConsumer);
        }
    }

    private <V> CacheSyncMonitor<V> getCacheSyncMonitor(String channel, Serializer<CacheSyncMessage> serializer,
                                                        CacheSyncManager syncManager, CacheType cacheType) {
        if (syncManager != null) {
            CacheMessagePublisher publisher = syncManager.getPublisher(channel);

            return new CacheSyncMonitor<>(sid, channel, cacheType, publisher, serializer);
        }
        return null;
    }

    private static String getChannel(String name, CacheProps cacheProps) {
        String channel = StringUtils.trim(cacheProps.getChannel());
        if (StringUtils.hasLength(channel)) {
            channel = channel + SYNC_SEPARATOR + name;
        }
        return channel;
    }

    private Serializer<CacheSyncMessage> getSyncMessageSerializer(CacheProps cacheProps) {
        return null;
    }

    private CacheSyncManager getCacheSyncManager(CacheProps cacheProps) {
        return null;
    }

    private CacheStatManager getCacheStatManager(CacheProps cacheProps) {
        // TODO 完善此类
        return new LogCacheStatManager(10000);
    }

    private Charset getCharset(String charset) {
        String charsetName = StringUtils.toUpperCase(charset);
        if (StringUtils.hasLength(charsetName)) {
            return Charset.forName(charsetName);
        }
        return StandardCharsets.UTF_8;
    }

    private <K, V> List<CacheMonitor<V>> getCacheMonitors(String name, Class<K> keyType, Class<V> valueType, CacheProps cacheProps) {
        // TODO 添加 CacheMonitors

        return new ArrayList<>();
    }

    /**
     * 获取
     *
     * @param keyType    键 class
     * @param cacheProps 缓存配置
     * @param <K>        键泛型参数
     * @return <p>{@link CacheLock}</p>
     * <p>如果未配置，默认返回 {@link LocalCacheLock}</p>
     * <p>如果有配置：配置 bean 正确，返回配置的 CacheLock；配置 bean 错误，抛出异常 {@link CacheConfigException} </p>
     */
    private <K> CacheLock<K> getCacheLock(Class<K> keyType, CacheProps cacheProps) {
        String beanId = cacheProps.getExtension().getCacheLock();
        if (StringUtils.hasText(beanId)) {
            CacheLockProvider provider = lockProviderMap.get(beanId);
            if (null == provider) {
                String errMsg = String.format("CacheLockProvider:[%s] doesn't exist.", beanId);
                throw new CacheConfigException(errMsg);
            }
            return provider.get(keyType, cacheProps);
        }

        return LocalCacheLockProvider.getINSTANCE().get(keyType, cacheProps);
    }

    /**
     * 获取
     *
     * @param keyType    键 class
     * @param cacheProps 缓存配置
     * @param <K>        键泛型参数
     * @return <p>{@link ContainsPredicate}</p>
     * <p>如果未配置，默认返回 AlwaysTrueContainsPredicate</p>
     * <p>如果有配置：配置 bean 正确，返回配置的 ContainsPredicate；配置 bean 错误，抛出异常 {@link CacheConfigException} </p>
     */
    private <K> ContainsPredicate<K> getContainsPredicate(Class<K> keyType, CacheProps cacheProps) {
        String beanId = StringUtils.trim(cacheProps.getExtension().getContainsPredicate());
        if (StringUtils.hasText(beanId)) {
            ContainsPredicateProvider provider = predicateProviderMap.get(beanId);
            if (null == provider) {
                String errMsg = String.format("ContainsPredicateProvider:[%s] doesn't exist.", beanId);
                throw new CacheConfigException(errMsg);
            }
            return provider.get(keyType, cacheProps);
        }
        return AlwaysTrueContainsPredicate.getINSTANCE();
    }

    /**
     * 通过缓存配置 获取 [缓存类型]
     *
     * @param cacheProps 缓存配置
     * @return {@link CacheType} 缓存类型
     */
    private CacheType getCacheType(CacheProps cacheProps) {
        String cacheType = StringUtils.toUpperCase(cacheProps.getCacheType());
        return CacheType.valueOf(cacheType);
    }

    /**
     * 通过缓存名称获取 [缓存配置]
     *
     * @param name 缓存名称
     * @return {@link CacheProps} 缓存配置
     */
    private CacheProps getOrCreateCacheProps(String name) {
        // 用户配置
        CacheProps userProps = cachePropsMap.get(name);
        if (userProps == null) {
            userProps = new CacheProps(name);
        }

        TemplateId templateId = TemplateId.T0;
        String templateStr = StringUtils.toUpperCase(userProps.getTemplate());
        if (StringUtils.hasLength(templateStr)) {
            templateId = TemplateId.valueOf(templateStr);
        }
        userProps.setTemplate(templateId.name());

        // 模板配置
        CacheProps template = templateMap.get(templateId);
        if (template == null) {
            throw new CacheConfigException("CacheProps: [" + templateId + "] doesn't exist.");
        }

        CacheProps cacheProps = CacheConfigUtil.merge(userProps, template);
        cachePropsMap.put(name, cacheProps);
        return cacheProps;
    }

    @Override
    public Collection<Cache<?, ?>> getAll() {
        return Collections.unmodifiableCollection(cacheMap.values());
    }

    @Override
    public Collection<String> getAllCacheNames() {
        return Collections.unmodifiableCollection(cacheMap.keySet());
    }

    public void addProvider(String beanId, CacheMonitorProvider provider) {
        this.monitorProviderMap.put(beanId, provider);
    }

    public void addProvider(String beanId, RemoteCacheStoreProvider provider) {
        this.remoteStoreProviderMap.put(beanId, provider);
    }

    public void addCacheProps(CacheProps cacheProps) {
        Objects.requireNonNull(cacheProps, "cacheProps must not be null");
        String name = StringUtils.trim(cacheProps.getName());
        if (StringUtils.hasLength(name)) {
            cacheProps.setName(name);
            cachePropsMap.put(name, cacheProps);
            return;
        }
        throw new CacheConfigException("cacheName must not be null or empty");
    }

    public void addCachePropsTemplate(TemplateId id, CacheProps cacheProps) {
        Objects.requireNonNull(id, "TemplateId must not be null");
        Objects.requireNonNull(cacheProps, "cacheProps must not be null");
        CacheProps previous = templateMap.put(id, cacheProps);
        if (previous != null) {
            throw new CacheConfigException("ID:[" + id + "], Template id is duplicate");
        }
    }

}
