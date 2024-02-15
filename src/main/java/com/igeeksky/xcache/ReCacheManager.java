package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheType;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.config.CacheConfigException;
import com.igeeksky.xcache.config.CacheConfigUtil;
import com.igeeksky.xcache.config.CacheConstants;
import com.igeeksky.xcache.config.props.CacheProps;
import com.igeeksky.xcache.config.props.TemplateId;
import com.igeeksky.xcache.extension.compress.Compressor;
import com.igeeksky.xcache.extension.compress.CompressorProvider;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.contains.ContainsPredicateProvider;
import com.igeeksky.xcache.extension.contains.TrueContainsPredicate;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.convertor.KeyConvertorProvider;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.lock.CacheLockProvider;
import com.igeeksky.xcache.extension.lock.LocalCacheLock;
import com.igeeksky.xcache.extension.lock.LocalCacheLockProvider;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProvider;
import com.igeeksky.xcache.extension.serializer.Serializer;
import com.igeeksky.xcache.extension.serializer.SerializerProvider;
import com.igeeksky.xcache.extension.statistic.CacheStatManager;
import com.igeeksky.xcache.extension.statistic.CacheStatMonitor;
import com.igeeksky.xcache.extension.sync.*;
import com.igeeksky.xcache.store.LocalStore;
import com.igeeksky.xcache.store.LocalStoreProvider;
import com.igeeksky.xcache.store.RemoteStore;
import com.igeeksky.xcache.store.RemoteStoreProvider;
import com.igeeksky.xtool.core.annotation.Perfect;
import com.igeeksky.xtool.core.lang.ArrayUtils;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class ReCacheManager implements CacheManager {

    private final String application;
    private final String sid = UUID.randomUUID().toString();
    private final ConcurrentMap<String, Cache<?, ?>> cacheMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CacheProps> userPropsMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<TemplateId, CacheProps> templates = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, SerializerProvider> serializers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CompressorProvider> compressors = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CacheSyncManager> syncProviders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CacheStatManager> statProviders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CacheLockProvider> lockProviders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CacheMonitorProvider> monitorProviders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, KeyConvertorProvider> keyConvertorProviders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ContainsPredicateProvider> predicateProviders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LocalStoreProvider> localStoreProviders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RemoteStoreProvider> remoteStoreProviders = new ConcurrentHashMap<>();

    public ReCacheManager(String application, Map<TemplateId, CacheProps> templates, Map<String, CacheProps> caches) {
        this.application = application;
        templates.forEach((id, props) -> {
            CacheProps defaultProps = CacheConfigUtil.defaultCacheProps(id);
            CacheProps template = CacheConfigUtil.copyProperties(props, defaultProps);
            this.templates.put(id, template);
        });
        this.userPropsMap.putAll(caches);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getOrCreateCache(String cacheName, Class<K> keyType, Class<V> valueType, Class<?>[] valueParams) {
        String name = StringUtils.trim(cacheName);
        requireNonNull(keyType, () -> "keyType must not be null");
        requireNonNull(valueType, () -> "valueType must not be null");
        requireNotEmpty(name, () -> "cacheName must not be null or empty");
        return (Cache<K, V>) cacheMap.computeIfAbsent(name, k -> createCache(k, keyType, valueType, valueParams));
    }

    private <K, V> Cache<K, V> createCache(String name, Class<K> keyType, Class<V> valueType, Class<?>[] valueParams) {
        // 1. 获取配置 @Perfect
        CacheProps props = this.getOrCreateCacheProps(name);

        // 2. 创建 CacheConfig @Perfect
        CacheConfig<K, V> config = CacheConfigUtil.createConfig(application, keyType, valueType, props);
        Charset charset = config.getCharset();

        // 3. 设置 CacheLock @Perfect
        config.setCacheLock(this.getCacheLock(props));

        // 4. 设置 ContainsPredicate @Perfect
        config.setContainsPredicate(this.getContainsPredicate(keyType, props));

        // 5. 设置 CacheMonitors @Perfect
        config.addMonitors(this.getCacheMonitors(keyType, valueType, props));

        // 6. 添加缓存统计类 @Perfect
        config.addMonitor(this.getCacheStatMonitor(props));

        // 7. 创建无操作缓存 @Perfect
        CacheType cacheType = this.getCacheType(props);
        if (cacheType == CacheType.NONE) {
            return new NoOpCache<>(config);
        }

        // 8. 添加 KeyConvertor @Perfect
        config.setKeyConvertor(this.getKeyConvertor(charset, props));

        // TODO 11. 添加 cacheLoader
        if (cacheType == CacheType.LOCAL) {
            // 9. 准备本地缓存的值的序列化器和压缩器
            prepareLocalCacheStore(charset, valueType, valueParams, props, config);
            // 10. 创建本地缓存
            LocalStore localStore = this.getLocalCacheStore(props, config);
            // 11. 添加缓存数据同步
            cacheSync(props, config, cacheType, localStore);
            return new OneLevelCache<>(config, localStore, null);
        }

        // 12. 添加远程缓存值序列化
        String remoteSerializer = props.getRemote().getValueSerializer();
        requireNotEmpty(remoteSerializer, () -> "Cache:[" + name + "] remote:\"value-serializer\" must not be null or empty");
        config.getRemoteConfig().setValueSerializer(this.getValueSerializer(name, remoteSerializer, charset, valueType, valueParams));

        // 13. 添加远程缓存值压缩器
        if (config.getRemoteConfig().isEnableCompressValue()) {
            config.getRemoteConfig().setValueCompressor(this.getValueCompressor(props.getRemote().getValueCompressor()));
        }

        // 14. 创建远程缓存
        if (cacheType == CacheType.REMOTE) {
            return new OneLevelCache<>(config, null, this.getRemoteCacheStore(props, config));
        }

        // 15. 创建两级缓存
        prepareLocalCacheStore(charset, valueType, valueParams, props, config);
        LocalStore localStore = this.getLocalCacheStore(props, config);
        RemoteStore remoteStore = this.getRemoteCacheStore(props, config);
        cacheSync(props, config, cacheType, localStore);
        return new TwoLevelCache<>(config, localStore, remoteStore);
    }

    /**
     * 通过缓存名称获取 [缓存配置]
     *
     * @param name 缓存名称
     * @return {@link CacheProps} 缓存配置
     */
    @Perfect
    private CacheProps getOrCreateCacheProps(String name) {
        // 获取用户配置
        CacheProps userProps = userPropsMap.computeIfAbsent(name, k -> new CacheProps(name));
        // 获取模板配置
        TemplateId templateId = getTemplateId(userProps.getTemplate());
        CacheProps template = templates.get(templateId);
        requireNonNull(template, () -> String.format("CacheProps template: [%s] doesn't exist.", templateId));
        // 用户配置 覆盖 模板配置
        return CacheConfigUtil.copyProperties(userProps, template.clone());
    }

    private static TemplateId getTemplateId(String idStr) {
        String template = StringUtils.toUpperCase(idStr);
        if (StringUtils.hasLength(template)) {
            return TemplateId.valueOf(template);
        }
        return CacheConstants.DEFAULT_TEMPLATE;
    }

    /**
     * @param props 缓存配置
     * @return <p>{@link CacheLock}</p>
     * <p>如果未配置，默认返回 {@link LocalCacheLock}</p>
     * <p>如果有配置：配置 bean 正确，返回配置的 CacheLock；配置 bean 错误，抛出异常 {@link CacheConfigException} </p>
     */
    @Perfect
    private CacheLock getCacheLock(CacheProps props) {
        String beanId = props.getExtension().getCacheLock();
        if (StringUtils.hasLength(beanId) && !Objects.equals(CacheConstants.NONE, StringUtils.toUpperCase(beanId))) {
            CacheLockProvider provider = lockProviders.get(beanId);
            requireNonNull(provider, () -> "CacheLockProvider:[" + beanId + "] is undefined.");
            CacheLock cacheLock = provider.get(props);
            requireNonNull(cacheLock, () -> "Unable to get lock from provider:[" + beanId + "].");
            return cacheLock;
        }
        return LocalCacheLockProvider.getInstance().get(props);
    }

    /**
     * @param keyType 键 class
     * @param props   缓存配置
     * @param <K>     键泛型参数
     * @return <p>{@link ContainsPredicate}</p>
     * <p>如果未配置，默认返回 AlwaysTrueContainsPredicate</p>
     * <p>如果有配置：配置 bean 正确，返回配置的 ContainsPredicate；配置 bean 错误，抛出异常 {@link CacheConfigException} </p>
     */
    @Perfect
    private <K> ContainsPredicate<K> getContainsPredicate(Class<K> keyType, CacheProps props) {
        String beanId = StringUtils.trim(props.getExtension().getContainsPredicate());
        if (StringUtils.hasLength(beanId) && !Objects.equals(CacheConstants.NONE, StringUtils.toUpperCase(beanId))) {
            ContainsPredicateProvider provider = predicateProviders.get(beanId);
            requireNonNull(provider, () -> "ContainsPredicateProvider:[" + beanId + "] is undefined.");
            ContainsPredicate<K> predicate = provider.get(keyType, props);
            requireNonNull(predicate, () -> "Unable to get predicate from provider:[" + beanId + "].");
            return predicate;
        }
        return TrueContainsPredicate.getInstance();
    }

    /**
     * @param keyType   键 class
     * @param valueType 值 class
     * @param props     缓存配置
     * @param <K>       键类型
     * @param <V>       键类型
     * @return cacheMonitors
     */
    @Perfect
    private <K, V> List<CacheMonitor<V>> getCacheMonitors(Class<K> keyType, Class<V> valueType, CacheProps props) {
        String beanIds = StringUtils.trim(props.getExtension().getCacheMonitors());
        if (!StringUtils.hasLength(beanIds) || Objects.equals(CacheConstants.NONE, StringUtils.toUpperCase(beanIds))) {
            return new ArrayList<>();
        }
        String[] ids = beanIds.split(",");
        if (ArrayUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<CacheMonitor<V>> monitors = new ArrayList<>(ids.length);
        for (String id : ids) {
            String beanId = StringUtils.trim(id);
            if (!StringUtils.hasLength(beanId)) {
                continue;
            }
            CacheMonitorProvider provider = monitorProviders.get(beanId);
            requireNonNull(provider, () -> "CacheMonitorProvider:[" + beanId + "] is undefined.");
            CacheMonitor<V> monitor = provider.get(props, keyType, valueType);
            requireNonNull(monitor, () -> "Unable to get monitor from provider:[" + beanId + "].");
            monitors.add(monitor);
        }
        return monitors;
    }

    @Perfect
    private <V> CacheStatMonitor<V> getCacheStatMonitor(CacheProps props) {
        String beanId = StringUtils.trim(props.getExtension().getCacheStat());
        if (!StringUtils.hasLength(beanId) || Objects.equals(CacheConstants.NONE, StringUtils.toUpperCase(beanId))) {
            return null;
        }
        CacheStatManager statManager = statProviders.get(beanId);
        requireNonNull(statManager, () -> "CacheStatManager:[" + beanId + "] is undefined.");
        CacheStatMonitor<V> monitor = new CacheStatMonitor<>(props.getName(), application);
        statManager.register(props.getName(), monitor);
        return monitor;
    }

    /**
     * 获取缓存类型
     *
     * @param props 缓存配置
     * @return {@link CacheType} 缓存类型
     */
    @Perfect
    private CacheType getCacheType(CacheProps props) {
        String cacheType = StringUtils.toUpperCase(props.getCacheType());
        return CacheType.valueOf(cacheType);
    }

    @Perfect
    private KeyConvertor getKeyConvertor(Charset charset, CacheProps props) {
        String name = props.getName();
        String beanId = props.getExtension().getKeyConvertor();
        requireNotEmpty(beanId, () -> "Cache:[" + name + "] \"key-convertor\" must not be null or empty.");
        KeyConvertorProvider provider = keyConvertorProviders.get(beanId);
        requireNonNull(provider, () -> "KeyConvertorProvider:[" + beanId + "] is undefined.");
        KeyConvertor keyConvertor = provider.get(charset);
        requireNonNull(keyConvertor, () -> "Unable to get keyConvertor from provider:[" + beanId + "].");
        return keyConvertor;
    }

    @Perfect
    private <V> Serializer<V> getValueSerializer(String name, String beanId, Charset charset,
                                                 Class<V> valueType, Class<?>[] valueParams) {
        SerializerProvider provider = serializers.get(beanId);
        requireNonNull(provider, () -> "Cache:[" + name + "] SerializerProvider:[" + beanId + "] is undefined.");

        Serializer<V> serializer = provider.get(name, charset, valueType, valueParams);
        requireNonNull(serializer, () -> "Cache:[" + name + "] Unable to get serializer from provider:[" + beanId + "].");
        return serializer;
    }

    @Perfect
    private Compressor getValueCompressor(String beanId) {
        CompressorProvider provider = compressors.get(beanId);
        requireNonNull(provider, () -> "CompressorProvider:[" + beanId + "] is undefined.");
        Compressor compressor = provider.get();
        requireNonNull(compressor, () -> "Unable to get compressor from provider:[" + beanId + "].");
        return compressor;
    }

    @Perfect
    private <K, V> void cacheSync(CacheProps props, CacheConfig<K, V> config, CacheType cacheType,
                                  LocalStore localStore) {
        String syncManagerId = props.getExtension().getCacheSync();
        CacheSyncManager syncManager = getCacheSyncManager(syncManagerId);
        if (syncManager != null) {
            String syncChannel = getCacheSyncChannel(props);
            CacheMessagePublisher publisher = syncManager.getPublisher(syncChannel);
            requireNonNull(publisher, () -> "Unable to get publisher from provider:[" + syncManagerId + "].");

            String name = props.getName();
            Charset charset = config.getCharset();
            Class<CacheSyncMessage> type = CacheSyncMessage.class;
            String serializerId = props.getExtension().getCacheSyncSerializer();
            requireNotEmpty(serializerId, () -> "Cache:[" + name + "] \"cache-sync-serializer\" must not be null or empty.");

            Serializer<CacheSyncMessage> serializer = getValueSerializer(name, serializerId, charset, type, null);
            requireNonNull(serializer, () -> "Unable to get serializer from provider:[" + serializerId + "].");

            config.addMonitor(new CacheSyncMonitor<>(sid, syncChannel, cacheType, publisher, serializer));
            CacheSyncConsumer consumer = new CacheSyncConsumer(sid, localStore, serializer);
            syncManager.register(syncChannel, consumer);
        }
    }

    @Perfect
    private CacheSyncManager getCacheSyncManager(String beanId) {
        if (StringUtils.hasLength(beanId) && !Objects.equals(CacheConstants.NONE, StringUtils.toUpperCase(beanId))) {
            CacheSyncManager syncManager = syncProviders.get(beanId);
            requireNonNull(syncManager, () -> "CacheSyncManager:[" + beanId + "] is undefined");
            return syncManager;
        }
        return null;
    }

    @Perfect
    private static String getCacheSyncChannel(CacheProps props) {
        String channel = props.getExtension().getCacheSyncChannel();
        requireNotEmpty(channel, () -> "Cache:[" + props.getName() + "], \"cache-sync-channel\" must not be null or empty");
        return channel + CacheConstants.SYNC_CHANNEL_INFIX + props.getName();
    }

    /**
     * 准备本地缓存的值的序列化器和压缩器
     *
     * @param charset   字符集
     * @param valueType 值类型 class
     * @param props     缓存配置
     * @param config    缓存配置
     * @param <K>       键类型
     * @param <V>       值类型
     */
    @Perfect
    private <K, V> void prepareLocalCacheStore(Charset charset, Class<V> valueType, Class<?>[] valueParams, CacheProps props, CacheConfig<K, V> config) {
        // 添加本地缓存值序列化器 @Perfect
        if (config.getLocalConfig().isEnableSerializeValue()) {
            String name = props.getName();
            String serializer = props.getLocal().getValueSerializer();
            config.getLocalConfig().setValueSerializer(this.getValueSerializer(name, serializer, charset, valueType, valueParams));
        }

        // 添加本地缓存值压缩器 @Perfect
        if (config.getLocalConfig().isEnableCompressValue()) {
            config.getLocalConfig().setValueCompressor(this.getValueCompressor(props.getLocal().getValueCompressor()));
        }
    }

    @Perfect
    private <K, V> LocalStore getLocalCacheStore(CacheProps props, CacheConfig<K, V> cacheConfig) {
        String name = props.getName();
        String beanId = props.getLocal().getCacheStore();
        requireNotEmpty(beanId, () -> "Cache:[" + name + "], \"local: cache-store\" must not be null or empty");

        LocalStoreProvider localStoreProvider = localStoreProviders.get(beanId);
        requireNonNull(localStoreProvider, () -> "LocalCacheStoreProvider:[" + beanId + "] is undefined");

        LocalStore localCacheStore = localStoreProvider.getLocalStore(cacheConfig);
        requireNonNull(localCacheStore, () -> "Cache:[" + name + "] Unable to get localCacheStore from provider:[" + beanId + "].");
        return localCacheStore;
    }

    @Perfect
    private <K, V> RemoteStore getRemoteCacheStore(CacheProps props, CacheConfig<K, V> config) {
        String name = props.getName();
        String beanId = props.getRemote().getCacheStore();
        requireNotEmpty(beanId, () -> "Cache:[" + name + "], \"remote: cache-store\" must not be null or empty");

        RemoteStoreProvider remoteStoreProvider = remoteStoreProviders.get(beanId);
        requireNonNull(remoteStoreProvider, () -> "RemoteCacheStoreProvider:[" + beanId + "] is undefined");

        RemoteStore remoteCacheStore = remoteStoreProvider.getRemoteCacheStore(config);
        requireNonNull(remoteCacheStore, () -> "Cache:[" + name + "] Unable to get remoteCacheStore from provider:[" + beanId + "].");
        return remoteCacheStore;
    }

    private static void requireNonNull(Object obj, Supplier<String> errMsg) {
        if (obj == null) {
            throw new CacheConfigException(errMsg.get());
        }
    }

    private static void requireNotEmpty(String str, Supplier<String> errMsg) {
        if (!StringUtils.hasLength(str)) {
            throw new CacheConfigException(errMsg.get());
        }
    }

    @Override
    public Collection<Cache<?, ?>> getAll() {
        return Collections.unmodifiableCollection(cacheMap.values());
    }

    @Override
    public Collection<String> getAllCacheNames() {
        return Collections.unmodifiableCollection(cacheMap.keySet());
    }

    public void addProvider(String beanId, SerializerProvider provider) {
        this.serializers.put(beanId, provider);
    }

    public void addProvider(String beanId, CompressorProvider provider) {
        this.compressors.put(beanId, provider);
    }

    public void addProvider(String beanId, CacheSyncManager provider) {
        this.syncProviders.put(beanId, provider);
    }

    public void addProvider(String beanId, CacheStatManager provider) {
        this.statProviders.put(beanId, provider);
    }

    public void addProvider(String beanId, CacheLockProvider provider) {
        this.lockProviders.put(beanId, provider);
    }

    public void addProvider(String beanId, CacheMonitorProvider provider) {
        this.monitorProviders.put(beanId, provider);
    }

    public void addProvider(String beanId, KeyConvertorProvider provider) {
        this.keyConvertorProviders.put(beanId, provider);
    }

    public void addProvider(String beanId, ContainsPredicateProvider provider) {
        this.predicateProviders.put(beanId, provider);
    }

    public void addProvider(String beanId, LocalStoreProvider provider) {
        this.localStoreProviders.put(beanId, provider);
    }

    public void addProvider(String beanId, RemoteStoreProvider provider) {
        this.remoteStoreProviders.put(beanId, provider);
    }
}
