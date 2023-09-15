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
 * 无操作缓存
 *
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-19
 */
public class NoOpCache<K, V> extends AbstractCache<K, V> {

    private final CacheMonitorProxy<K, V> cacheMonitor = new CacheMonitorProxy<>();

    public NoOpCache(CacheConfig<K, V> config) {
        super(config);
        this.cacheMonitor.addCacheMonitors(config.getMonitors());
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key) {
        return Mono.fromSupplier(() -> {
            cacheMonitor.afterGet(key, null, CacheLevel.L1);
            return null;
        });
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key, CacheLoader<K, V> cacheLoader) {
        return cacheLoader.load(key)
                .doOnSuccess(value -> cacheMonitor.afterLoad(key, value))
                .map(CacheValue::new);
    }

    @Override
    protected Flux<KeyValue<K, CacheValue<V>>> doGetAll(Set<? extends K> keys) {
        return Flux.fromIterable(keys)
                .doOnNext(k -> cacheMonitor.afterGet(k, null, CacheLevel.L1))
                .flatMap(k -> Flux.empty());
    }

    @Override
    protected Mono<Void> doPut(K key, V value) {
        return Mono.just(key)
                .doOnSuccess(k -> cacheMonitor.afterPut(k, value, CacheLevel.L1))
                .flatMap(k -> Mono.empty());
    }

    @Override
    protected Mono<Void> doPutAll(Map<? extends K, ? extends V> keyValues) {
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doRemove(K key) {
        return Mono.empty();
    }

    @Override
    protected Mono<Void> doRemoveAll(Set<? extends K> keys) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> clear() {
        return Mono.empty();
    }

}
