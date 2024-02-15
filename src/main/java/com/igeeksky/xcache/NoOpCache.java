package com.igeeksky.xcache;


import com.igeeksky.xcache.extension.loader.CacheLoader;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.common.StoreType;
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
        return Mono.just(key)
                .doOnSuccess(k -> cacheMonitor.afterGet(k, null, StoreType.NOOP))
                .flatMap(k -> Mono.empty());
    }

    @Override
    protected Mono<CacheValue<V>> doLoad(K key, String storeKey, CacheLoader<K, V> cacheLoader) {
        return cacheLoader.load(key)
                .doOnSuccess(value -> cacheMonitor.afterLoad(storeKey, value))
                .map(CacheValue::new);
    }

    @Override
    protected Flux<KeyValue<String, CacheValue<V>>> doGetAll(Set<String> keys) {
        return Flux.fromIterable(keys)
                .doOnNext(k -> cacheMonitor.afterGet(k, null, StoreType.NOOP))
                .flatMap(k -> Flux.empty());
    }

    @Override
    protected Mono<Void> doPut(String key, V value) {
        return Mono.just(key).doOnSuccess(k -> cacheMonitor.afterPut(k, value, StoreType.NOOP)).then();
    }

    @Override
    protected Mono<Void> doPutAll(Map<String, ? extends V> keyValues) {
        return Mono.just(keyValues).doOnSuccess(kvs -> cacheMonitor.afterPutAll(kvs, StoreType.NOOP)).then();
    }

    @Override
    protected Mono<Void> doRemove(String key) {
        return Mono.just(key).doOnSuccess(k -> cacheMonitor.afterRemove(k, StoreType.NOOP)).then();
    }

    @Override
    protected Mono<Void> doRemoveAll(Set<String> keys) {
        return Mono.just(keys).doOnSuccess(ks -> cacheMonitor.afterRemoveAll(ks, StoreType.NOOP)).then();
    }

    @Override
    public Mono<Void> clear() {
        return Mono.empty().doOnSuccess(vod -> cacheMonitor.afterClear(StoreType.NOOP)).then();
    }

}
