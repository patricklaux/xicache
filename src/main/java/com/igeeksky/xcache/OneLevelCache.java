package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheLoader;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.common.StoreType;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProxy;
import com.igeeksky.xcache.store.LocalCacheStore;
import com.igeeksky.xcache.store.RemoteCacheStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-08-23
 */
public class OneLevelCache<K, V> extends AbstractCache<K, V> {


    private final LocalCacheStore localStore;

    private final RemoteCacheStore remoteStore;

    private final CacheMonitorProxy<V> cacheMonitor = new CacheMonitorProxy<>();

    public OneLevelCache(CacheConfig<K, V> config, LocalCacheStore localStore, RemoteCacheStore remoteStore) {
        super(config);
        this.localStore = localStore;
        this.remoteStore = remoteStore;
        this.cacheMonitor.addCacheMonitors(config.getMonitors());
    }

    @Override
    protected Mono<CacheValue<V>> doGet(String key) {
        if (localStore != null) {
            return localStore.get(key)
                    .flatMap(this::fromLocalStoreValue)
                    .doOnNext(value -> cacheMonitor.afterGet(key, value, StoreType.LOCAL));
        } else {
            return remoteStore.get(key)
                    .flatMap(this::fromRemoteStoreValue)
                    .doOnNext(value -> cacheMonitor.afterGet(key, value, StoreType.REMOTE));
        }
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key, String storeKey, CacheLoader<K, V> cacheLoader) {
        return cacheLoader.load(key)
                .doOnSuccess(value -> this.doPut(storeKey, value))
                .doOnSuccess(value -> cacheMonitor.afterLoad(storeKey, value))
                .map(CacheValue::new);
    }

    @Override
    public Flux<KeyValue<String, CacheValue<V>>> doGetAll(Set<String> keys) {
        if (localStore != null) {
            return localStore.getAll(keys)
                    .flatMap(kv -> fromLocalStoreValue(kv.getValue()).map(v -> new KeyValue<>(kv.getKey(), v)))
                    .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), StoreType.LOCAL));
        } else {
            return remoteStore.getAll(keys)
                    .flatMap(kv -> fromRemoteStoreValue(kv.getValue()).map(v -> new KeyValue<>(kv.getKey(), v)))
                    .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), StoreType.REMOTE));
        }
    }

    @Override
    protected Mono<Void> doPut(String key, V value) {
        if (localStore != null) {
            return localStore.put(key, Mono.justOrEmpty(toLocalStoreValue(value)))
                    .doOnSuccess(vod -> cacheMonitor.afterPut(key, value, StoreType.LOCAL));
        } else {
            return remoteStore.put(key, Mono.justOrEmpty(toRemoteStoreValue(value)))
                    .doOnSuccess(vod -> cacheMonitor.afterPut(key, value, StoreType.REMOTE));
        }
    }

    @Override
    protected Mono<Void> doPutAll(Map<String, ? extends V> keyValues) {
        if (localStore != null) {
            return Mono.just(keyValues)
                    .map(kvs -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        kvs.forEach((k, v) -> map.put(k, toLocalStoreValue(v)));
                        return map;
                    })
                    .flatMap(map ->
                            localStore.putAll(Mono.just(map))
                                    .doOnSuccess(vod -> cacheMonitor.afterPutAll(keyValues, StoreType.LOCAL))
                    );
        } else {
            return Mono.just(keyValues)
                    .map(kvs -> {
                        Map<String, byte[]> map = new LinkedHashMap<>();
                        kvs.forEach((k, v) -> map.put(k, toRemoteStoreValue(v)));
                        return map;
                    })
                    .flatMap(map ->
                            remoteStore.putAll(Mono.just(map))
                                    .doOnSuccess(vod -> cacheMonitor.afterPutAll(keyValues, StoreType.REMOTE))
                    );
        }
    }

    @Override
    protected Mono<Void> doRemove(String key) {
        if (localStore != null) {
            return localStore.remove(key)
                    .doOnSuccess(vod -> cacheMonitor.afterRemove(key, StoreType.LOCAL));
        } else {
            return remoteStore.remove(key)
                    .doOnSuccess(vod -> cacheMonitor.afterRemove(key, StoreType.REMOTE));
        }

    }

    @Override
    protected Mono<Void> doRemoveAll(Set<String> keys) {
        if (localStore != null) {
            return Mono.just(keys)
                    .flatMap(localStore::removeAll)
                    .doOnSuccess(vod -> cacheMonitor.afterRemoveAll(keys, StoreType.LOCAL));
        } else {
            return Mono.just(keys)
                    .flatMap(remoteStore::removeAll)
                    .doOnSuccess(vod -> cacheMonitor.afterRemoveAll(keys, StoreType.REMOTE));
        }
    }

    @Override
    public Mono<Void> clear() {
        if (localStore != null) {
            return localStore.clear()
                    .doOnSuccess(vod -> cacheMonitor.afterClear(StoreType.LOCAL));
        } else {
            return remoteStore.clear()
                    .doOnSuccess(vod -> cacheMonitor.afterClear(StoreType.REMOTE));
        }
    }

}