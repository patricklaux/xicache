package com.igeeksky.xcache.support.caffeine;

import com.igeeksky.xcache.store.CacheStoreProvider;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.store.AbstractCacheStore;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class CaffeineCacheStoreProvider implements CacheStoreProvider {

    public static final String PROVIDER_NAME = "CaffeineCacheStoreProvider";

    @Override
    public <K, V> AbstractCacheStore<K, V> get(CacheConfig<K, V> cacheConfig) {
        return null;
    }

}
