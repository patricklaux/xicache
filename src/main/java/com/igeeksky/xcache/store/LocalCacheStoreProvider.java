package com.igeeksky.xcache.store;

import com.igeeksky.xcache.Provider;
import com.igeeksky.xcache.config.CacheConfig;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
public interface LocalCacheStoreProvider extends Provider {

    /**
     * 根据配置生成缓存实例
     *
     * @param config 缓存配置
     * @param <K>    键类型
     * @param <V>    值类型
     * @return 缓存存储器
     */
    <K, V> LocalCacheStore getLocalCacheStore(CacheConfig<K, V> config);

}
