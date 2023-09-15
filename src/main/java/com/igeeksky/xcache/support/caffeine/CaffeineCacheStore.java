package com.igeeksky.xcache.support.caffeine;


import com.github.benmanes.caffeine.cache.Cache;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.StoreType;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.store.AbstractLocalCacheStore;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-22
 */
public class CaffeineCacheStore<K, V> extends AbstractLocalCacheStore<K, V> {

    private final Cache<Object, CacheValue<Object>> cache;

    public CaffeineCacheStore(CacheConfig<K, V> config, Cache<Object, CacheValue<Object>> cache) {
        super(config);
        this.cache = cache;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.LOCAL;
    }

    @Override
    protected Mono<CacheValue<Object>> doStoreGet(Object key) {
        return Mono.justOrEmpty(cache.getIfPresent(key));
        // return cache.getIfPresent(key);
    }

    @Override
    protected void doStorePut(Object key, CacheValue<Object> cacheValue) {
        cache.put(key, cacheValue);
    }

    protected void doStorePutAll(Map<Object, CacheValue<Object>> keyValues) {
        cache.putAll(keyValues);
    }

    @Override
    protected void doStoreRemove(Object key) {
        cache.invalidate(key);
    }

    @Override
    protected void doStoreRemoveAll(Set<Object> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public Mono<Void> clear() {
        return Mono.fromSupplier(() -> {
            cache.invalidateAll();
            return null;
        });
    }

}
