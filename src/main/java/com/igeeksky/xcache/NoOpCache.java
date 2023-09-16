package com.igeeksky.xcache;


import com.igeeksky.xcache.common.*;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProxy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

/**
 * 无操作缓存
 *
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-19
 */
public class NoOpCache<K, V> extends AbstractCache<K, V> {

    private final CacheMonitorProxy<V> cacheMonitor = new CacheMonitorProxy<>();

    public NoOpCache(CacheConfig<K, V> config) {
        super(config);
        this.cacheMonitor.addCacheMonitors(config.getMonitors());
    }

    @Override
    protected Mono<CacheValue<V>> doGet(String key) {
        return Mono.fromSupplier(() -> {
            cacheMonitor.afterGet(key, null, StoreType.LOCAL);
            return null;
        });
    }


    @Override
    protected Mono<CacheValue<V>> doGet(K key, String storeKey, CacheLoader<K, V> cacheLoader) {
        return cacheLoader.load(key)
                .doOnSuccess(value -> cacheMonitor.afterLoad(storeKey, value))
                .map(CacheValue::new);
    }

    @Override
    protected Flux<KeyValue<String, CacheValue<V>>> doGetAll(Set<String> keys) {
        return Flux.fromIterable(keys)
                .doOnNext(k -> cacheMonitor.afterGet(k, null, StoreType.LOCAL))
                .flatMap(k -> Flux.empty());
    }

    @Override
    protected Mono<Void> doPut(String key, V value) {
        return Mono.just(key)
                .doOnSuccess(k -> cacheMonitor.afterPut(k, value, StoreType.LOCAL))
                .flatMap(k -> Mono.empty());
    }

    @Override
    protected Mono<Void> doPutAll(Map<String, ? extends V> keyValues) {
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doRemove(String key) {
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doRemoveAll(Set<String> keys) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> clear() {
        return Mono.empty();
    }

}
