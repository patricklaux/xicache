package com.igeeksky.xcache.support.caffeine;


import com.github.benmanes.caffeine.cache.Cache;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.store.LocalStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-22
 */
public class CaffeineStore<K, V> implements LocalStore {

    private static final String STORE_NAME = "Caffeine";

    private final Cache<String, CacheValue<Object>> cache;

    public CaffeineStore(Cache<String, CacheValue<Object>> cache) {
        this.cache = cache;
    }

    @Override
    public Mono<CacheValue<Object>> get(String key) {
        return Mono.justOrEmpty(cache.getIfPresent(key));
    }

    @Override
    public Flux<KeyValue<String, CacheValue<Object>>> getAll(Set<? extends String> keys) {
        return Flux.fromIterable(cache.getAllPresent(keys).entrySet())
                .map(entry -> new KeyValue<>(entry.getKey(), entry.getValue()));
    }

    @Override
    public Mono<Void> doPut(String key, Mono<CacheValue<Object>> value) {
        return value.doOnSuccess(cacheValue -> cache.put(key, cacheValue)).then();
    }

    @Override
    public Mono<Void> doPutAll(Mono<Map<String, CacheValue<Object>>> mono) {
        return mono.doOnSuccess(cache::putAll).then();
    }

    @Override
    public Mono<Void> remove(String key) {
        return Mono.just(key).doOnSuccess(cache::invalidate).then();
    }

    @Override
    public Mono<Void> removeAll(Set<? extends String> keys) {
        return Mono.just(keys).doOnSuccess(cache::invalidateAll).then();
    }

    @Override
    public Mono<Void> clear() {
        return Mono.empty().doOnSuccess(vod -> cache.invalidateAll()).then();
    }

    @Override
    public String getStoreName() {
        return STORE_NAME;
    }

}
