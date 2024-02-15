package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.common.StoreType;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.loader.CacheLoader;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProxy;
import com.igeeksky.xcache.store.LocalStore;
import com.igeeksky.xcache.store.RemoteStore;
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

    private final StoreType storeType;

    private final LocalStore localStore;

    private final RemoteStore remoteStore;

    private final CacheMonitorProxy<V> cacheMonitor = new CacheMonitorProxy<>();

    public OneLevelCache(CacheConfig<K, V> config, LocalStore localStore, RemoteStore remoteStore) {
        super(config);
        this.localStore = localStore;
        this.remoteStore = remoteStore;
        this.storeType = (localStore != null) ? StoreType.LOCAL : StoreType.REMOTE;
        this.cacheMonitor.addCacheMonitors(config.getMonitors());
    }

    @Override
    protected Mono<CacheValue<V>> doGet(String key) {
        if (localStore != null) {
            return localStore.get(key)
                    .flatMap(this::fromLocalStoreValue)
                    .doOnNext(value -> cacheMonitor.afterGet(key, value, storeType));
        } else {
            return remoteStore.get(key)
                    .flatMap(this::fromRemoteStoreValue)
                    .doOnNext(value -> cacheMonitor.afterGet(key, value, storeType));
        }
    }

    @Override
    protected Mono<CacheValue<V>> doLoad(K key, String storeKey, CacheLoader<K, V> cacheLoader) {
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
                    .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), storeType));
        } else {
            return remoteStore.getAll(keys)
                    .flatMap(kv -> fromRemoteStoreValue(kv.getValue()).map(v -> new KeyValue<>(kv.getKey(), v)))
                    .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), storeType));
        }
    }

    @Override
    protected Mono<Void> doPut(String key, V value) {
        if (localStore != null) {
            return localStore.put(key, Mono.justOrEmpty(toLocalStoreValue(value)))
                    .doOnSuccess(vod -> cacheMonitor.afterPut(key, value, storeType));
        } else {
            return remoteStore.put(key, Mono.justOrEmpty(toRemoteStoreValue(value)))
                    .doOnSuccess(vod -> cacheMonitor.afterPut(key, value, storeType));
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
                                    .doOnSuccess(vod -> cacheMonitor.afterPutAll(keyValues, storeType))
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
                                    .doOnSuccess(vod -> cacheMonitor.afterPutAll(keyValues, storeType))
                    );
        }
    }

    @Override
    protected Mono<Void> doRemove(String key) {
        if (localStore != null) {
            return localStore.remove(key)
                    .doOnSuccess(vod -> cacheMonitor.afterRemove(key, storeType));
        } else {
            return remoteStore.remove(key)
                    .doOnSuccess(vod -> cacheMonitor.afterRemove(key, storeType));
        }

    }

    @Override
    protected Mono<Void> doRemoveAll(Set<String> keys) {
        if (localStore != null) {
            return Mono.just(keys)
                    .flatMap(localStore::removeAll)
                    .doOnSuccess(vod -> cacheMonitor.afterRemoveAll(keys, storeType));
        } else {
            return Mono.just(keys)
                    .flatMap(remoteStore::removeAll)
                    .doOnSuccess(vod -> cacheMonitor.afterRemoveAll(keys, storeType));
        }
    }

    @Override
    public Mono<Void> clear() {
        if (localStore != null) {
            return localStore.clear()
                    .doOnSuccess(vod -> cacheMonitor.afterClear(storeType));
        } else {
            return remoteStore.clear()
                    .doOnSuccess(vod -> cacheMonitor.afterClear(storeType));
        }
    }

}