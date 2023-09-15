package com.igeeksky.xcache.store;


import com.igeeksky.xcache.Provider;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.store.AbstractCacheStore;

/**
 * 缓存存储器工厂类接口
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-10
 */
public interface CacheStoreProvider extends Provider {

    /**
     * 根据配置生成缓存实例
     *
     * @param cacheConfig 缓存配置
     * @param <K>         键类型
     * @param <V>         值类型
     * @return 缓存存储器
     */
    <K, V> AbstractCacheStore<K, V> get(CacheConfig<K, V> cacheConfig);

}
