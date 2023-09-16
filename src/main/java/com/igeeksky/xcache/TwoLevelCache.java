package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheLoader;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.common.StoreType;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProxy;
import com.igeeksky.xcache.store.LocalCacheStore;
import com.igeeksky.xcache.store.RemoteCacheStore;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 两级组合缓存
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-03
 */
public class TwoLevelCache<K, V> extends AbstractCache<K, V> {

    private final LocalCacheStore localStore;

    private final RemoteCacheStore remoteStore;

    private final CacheMonitorProxy<V> cacheMonitor = new CacheMonitorProxy<>();

    public TwoLevelCache(CacheConfig<K, V> config, LocalCacheStore localStore, RemoteCacheStore remoteStore) {
        super(config);
        this.localStore = localStore;
        this.remoteStore = remoteStore;
        this.cacheMonitor.addCacheMonitors(config.getMonitors());
    }

    @Override
    protected Mono<CacheValue<V>> doGet(String key) {
        return localStore.get(key)
                .flatMap(this::fromLocalStoreValue)
                .doOnSuccess(cv -> cacheMonitor.afterGet(key, cv, StoreType.LOCAL))
                .switchIfEmpty(remoteStore.get(key)
                        .flatMap(this::fromRemoteStoreValue)
                        .doOnSuccess(value -> cacheMonitor.afterGet(key, value, StoreType.REMOTE))
                        .filter(Objects::nonNull)
                        .doOnNext(cv -> localStore.put(key, Mono.justOrEmpty(this.toLocalStoreValue(cv.getValue()))))
                );
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key, String storeKey, CacheLoader<K, V> cacheLoader) {
        return cacheLoader.load(key)
                .doOnSuccess(value -> this.doPut(storeKey, value))
                .doOnSuccess(value -> cacheMonitor.afterLoad(storeKey, value))
                .map(CacheValue::new);
    }

    @Override
    protected Flux<KeyValue<String, CacheValue<V>>> doGetAll(Set<String> keys) {
        return localStore.getAll(keys)
                .doOnNext(kv -> keys.remove(kv.getKey()))
                .flatMap(kv -> fromLocalStoreValue(kv.getValue()).map(v -> new KeyValue<>(kv.getKey(), v)))
                .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), StoreType.LOCAL))
                .collect(() -> new ArrayList<KeyValue<String, CacheValue<V>>>(keys.size()), ArrayList::add)
                .filter(firstList -> CollectionUtils.isNotEmpty(keys))
                .flatMapMany(firstList -> remoteStore.getAll(keys)
                        .flatMap(kv -> fromRemoteStoreValue(kv.getValue()).map(v -> new KeyValue<>(kv.getKey(), v)))
                        .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), StoreType.REMOTE))
                        .filter(KeyValue::hasValue)
                        .doOnNext(kv -> localStore.put(kv.getKey(), Mono.justOrEmpty(kv.getValue().getValue())))
                        .concatWith(Flux.fromIterable(firstList))
                );
    }

    @Override
    protected Mono<Void> doPut(String key, V value) {
        return remoteStore.put(key, Mono.justOrEmpty(toRemoteStoreValue(value)))
                .mergeWith(localStore.put(key, Mono.justOrEmpty(toLocalStoreValue(value))))
                .then();
    }

    @Override
    protected Mono<Void> doPutAll(Map<String, ? extends V> keyValues) {
        return Mono.just(keyValues)
                .map(kvs -> {
                    Map<String, byte[]> map = new LinkedHashMap<>();
                    kvs.forEach((k, v) -> map.put(k, toRemoteStoreValue(v)));
                    return map;
                })
                .flatMap(map -> remoteStore.putAll(Mono.just(map))
                        .doOnSuccess(vod -> cacheMonitor.afterPutAll(keyValues, StoreType.REMOTE)))
                .mergeWith(Mono.just(keyValues)
                        .map(kvs -> {
                            Map<String, Object> map = new LinkedHashMap<>();
                            kvs.forEach((k, v) -> map.put(k, toLocalStoreValue(v)));
                            return map;
                        })
                        .flatMap(map -> localStore.putAll(Mono.just(map))
                                .doOnSuccess(vod -> cacheMonitor.afterPutAll(keyValues, StoreType.LOCAL)))
                )
                .then();
    }

    @Override
    protected Mono<Void> doRemove(String key) {
        return remoteStore.remove(key).mergeWith(localStore.remove(key)).then();
    }

    @Override
    protected Mono<Void> doRemoveAll(Set<String> keys) {
        return remoteStore.removeAll(keys).mergeWith(localStore.removeAll(keys)).then();
    }

    @Override
    public Mono<Void> clear() {
        return remoteStore.clear()
                .doOnSuccess(vod -> cacheMonitor.afterClear(StoreType.REMOTE))
                .mergeWith(vod -> localStore.clear().doOnSuccess(vod2 -> cacheMonitor.afterClear(StoreType.LOCAL)))
                .then();
    }

}