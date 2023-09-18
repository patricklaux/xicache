package com.igeeksky.xcache;

import com.igeeksky.xcache.config.*;
import com.igeeksky.xcache.extension.contains.AlwaysTrueContainsPredicate;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.contains.ContainsPredicateProvider;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.lock.CacheLockProvider;
import com.igeeksky.xcache.extension.lock.LocalCacheLock;
import com.igeeksky.xcache.extension.lock.LocalCacheLockProvider;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProvider;
import com.igeeksky.xcache.extension.sync.CacheMessagePublisher;
import com.igeeksky.xcache.extension.sync.CacheSyncProvider;
import com.igeeksky.xcache.store.LocalCacheStore;
import com.igeeksky.xcache.store.LocalCacheStoreProvider;
import com.igeeksky.xcache.store.RemoteCacheStoreProvider;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class MultiLevelCacheManager implements CacheManager {

    private final String sid = UUID.randomUUID().toString();

    private final ConcurrentMap<String, Cache<?, ?>> cacheMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheProps> cachePropsMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<CacheTemplate, CacheTemplateProps> cacheTemplateMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheLockProvider> lockProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheSyncProvider> syncProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, CacheMonitorProvider> monitorProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, LocalCacheStoreProvider> localStoreProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, RemoteCacheStoreProvider> remoteStoreProviderMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ContainsPredicateProvider> predicateProviderMap = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> get(String name, Class<K> keyType, Class<V> valueType) {
        return (Cache<K, V>) cacheMap.computeIfAbsent(name, k -> createCache(k, keyType, valueType));
    }

    public <K, V> Cache<K, V> createCache(String name, Class<K> keyType, Class<V> valueType) {
        // 1. 获取配置
        CacheProps cacheProps = this.getCacheProps(name);
        // 2. 创建 CacheConfig
        CacheConfig<K, V> cacheConfig = new CacheConfig<>(name);
        cacheConfig.setKeyType(keyType);
        cacheConfig.setValueType(valueType);

        // 3. 设置 ContainsPredicate
        cacheConfig.setContainsPredicate(this.getkContainsPredicate(keyType, cacheProps));

        // 4. 设置 CacheLock
        cacheConfig.setCacheLock(this.getCacheLock(keyType, cacheProps));

        // 5. 设置 CacheMonitors
        cacheConfig.setMonitors(this.getCacheMonitors(name, keyType, valueType, cacheProps));

        // 6. 通过 CacheProps 判断是否需要创建 CacheStore
        CacheType cacheType = this.getCacheType(cacheProps);
        if (cacheType == CacheType.NONE) {
            return new NoOpCache<>(cacheConfig);
        }

        // cacheConfig.setCompressor()

        // cacheConfig.setKeyConvertor()

        // cacheConfig.setValueSerializer()

        // 生成 channel
        String channel = StringUtils.trim(cacheProps.getChannel());
        if (StringUtils.hasLength(channel)) {
            channel = channel + ":" + name;
        }

        // 获取 CacheMessagePublisher
        CacheSyncProvider provider = syncProviderMap.get(cacheProps.getExtension().getCacheSync());
        CacheMessagePublisher publisher = provider.getPublisher(channel);

        // 通过 CacheProps 获取 CacheStoreProvider
        if (cacheType == CacheType.LOCAL) {
            // 返回本地缓存
            CacheProps.Config first = cacheProps.getFirst();
            String beanId = first.getCacheStore();
            LocalCacheStoreProvider localStoreProvider = localStoreProviderMap.get(beanId);
            // 构建配置

            // 获取缓存
            LocalCacheStore localCacheStore = localStoreProvider.getLocalCacheStore(cacheConfig);

        }

        if (cacheType == CacheType.REMOTE) {
            // 返回远程缓存
            CacheProps.Config second = cacheProps.getSecond();
            String beanId = second.getCacheStore();
            RemoteCacheStoreProvider cacheStoreProvider = remoteStoreProviderMap.get(beanId);
            cacheStoreProvider.getRemoteCacheStore(cacheConfig);

        }

        if (cacheType == CacheType.BOTH) {
            // 返回两级缓存

        }

        return null;
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
    private <K> ContainsPredicate<K> getkContainsPredicate(Class<K> keyType, CacheProps cacheProps) {
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
        String cacheTypeStr = StringUtils.toUpperCase(cacheProps.getCacheType());
        return CacheType.valueOf(cacheTypeStr);
    }

    /**
     * 通过缓存名称获取 [缓存配置]
     *
     * @param name 缓存名称
     * @return {@link CacheProps} 缓存配置
     */
    private CacheProps getCacheProps(String name) {
        CacheProps cacheProps = cachePropsMap.get(name);

        if (cacheProps == null) {
            cacheProps = new CacheProps(name);
        }

        CacheTemplate template = CacheTemplate.T0;
        String templateStr = StringUtils.toUpperCase(cacheProps.getTemplate());
        if (StringUtils.hasText(templateStr)) {
            template = CacheTemplate.valueOf(templateStr);
        } else {
            cacheProps.setTemplate(template.name());
        }

        CacheTemplateProps templateProps = cacheTemplateMap.get(template);
        if (templateProps == null) {
            String msg = String.format("CacheTemplateProps: [%s] doesn't exist.", template);
            throw new CacheConfigException(msg);
        }

        cacheProps.deepClone(templateProps);
        cachePropsMap.put(name, cacheProps);
        return cacheProps;
    }

    @Override
    public Collection<Cache<?, ?>> getAll() {
        return null;
    }

    @Override
    public Collection<String> getAllCacheNames() {
        return null;
    }

    public void addProvider(String beanId, CacheMonitorProvider provider) {
        this.monitorProviderMap.put(beanId, provider);
    }

    public void addProvider(String beanId, RemoteCacheStoreProvider provider) {
        this.remoteStoreProviderMap.put(beanId, provider);
    }

}
