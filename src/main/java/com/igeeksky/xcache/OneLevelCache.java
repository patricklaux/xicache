package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheLevel;
import com.igeeksky.xcache.common.CacheLoader;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProxy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-08-23
 */
public class OneLevelCache<K, V> extends AbstractCache<K, V> {

    private final ReactiveCache<K, V> cache;

    private final CacheMonitorProxy<K, V> cacheMonitor = new CacheMonitorProxy<>();

    public OneLevelCache(CacheConfig<K, V> config, ReactiveCache<K, V> cache) {
        super(config);
        this.cache = cache;
        this.cacheMonitor.addCacheMonitors(config.getMonitors());
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key) {
        return cache.get(key)
                .doOnNext(value -> cacheMonitor.afterGet(key, value, CacheLevel.L1));
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key, CacheLoader<K, V> cacheLoader) {
        return cacheLoader.load(key)
                .doOnSuccess(value -> this.doPut(key, value))
                .doOnSuccess(value -> cacheMonitor.afterLoad(key, value))
                .map(CacheValue::new);
    }

    @Override
    public Flux<KeyValue<K, CacheValue<V>>> doGetAll(Set<? extends K> keys) {
        return cache.getAll(keys)
                .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), CacheLevel.L1));
    }

    @Override
    protected Mono<Void> doPut(K key, V value) {
        return cache.put(key, Mono.justOrEmpty(value))
                .doOnSuccess(vod -> cacheMonitor.afterPut(key, value, CacheLevel.L1));
    }

    @Override
    protected Mono<Void> doPutAll(Map<? extends K, ? extends V> keyValues) {
        return cache.putAll(Mono.just(keyValues))
                .doOnSuccess(vod -> cacheMonitor.afterPutAll(keyValues, CacheLevel.L1));
    }

    @Override
    protected Mono<Void> doRemove(K key) {
        return cache.remove(key)
                .doOnSuccess(vod -> cacheMonitor.afterRemove(key, CacheLevel.L1));
    }

    @Override
    protected Mono<Void> doRemoveAll(Set<? extends K> keys) {
        return Mono.just(keys)
                .flatMap(cache::removeAll)
                .doOnSuccess(vod -> cacheMonitor.afterRemoveAll(keys, CacheLevel.L1));
    }

    @Override
    public Mono<Void> clear() {
        return cache.clear()
                .doOnSuccess(vod -> cacheMonitor.afterClear(CacheLevel.L1));
    }

}