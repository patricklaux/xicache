package com.igeeksky.xcache;

import com.igeeksky.xcache.common.*;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.Compressor;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProxy;
import com.igeeksky.xcache.extension.serializer.Serializer;
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

    private final ReactiveCache<K, V> firstCache;
    private final ReactiveCache<K, V> secondCache;
    private final CacheMonitorProxy<K, V> cacheMonitor = new CacheMonitorProxy<>();

    private ReactiveCache<String, Object> local;
    private ReactiveCache<String, byte[]> remote;

    private KeyConvertor keyConvertor;

    private Serializer<V> localValueSerializer;

    private Compressor localValueCompressor;

    private Serializer<V> remoteValueSerializer;

    private Compressor remoteValueCompressor;

    private boolean serializeLocalValue;

    private boolean compressLocalValue;

    private boolean compressRemoteValue;

    private boolean useKeyPrefix = true;

    private boolean allowNullValue;

    public TwoLevelCache(CacheConfig<K, V> config, ReactiveCache<K, V> firstCache, ReactiveCache<K, V> secondCache) {
        super(config);
        this.firstCache = firstCache;
        this.secondCache = secondCache;
        this.cacheMonitor.addCacheMonitors(config.getMonitors());
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key) {
        Mono.just(key)
                .map(this::toLocalStoreKey)
                .flatMap(k -> local.get(k)
                        .flatMap(this::fromLocalStoreValue)
                        .doOnSuccess(value -> cacheMonitor.afterGet(key, value, CacheLevel.L1))
                        .switchIfEmpty(remote.get(k)
                                .flatMap(this::fromRemoteStoreValue)
                                .doOnSuccess(value -> cacheMonitor.afterGet(key, value, CacheLevel.L2))
                                .filter(Objects::nonNull)
                                .doOnNext(cacheValue -> {
                                    Object localStoreValue = toLocalStoreValue(cacheValue.getValue());
                                    local.put(k, Mono.justOrEmpty(localStoreValue));
                                })
                        ));

        // CacheValue 是不需要的，本地缓存通过 NullValue来代替

        return firstCache.get(key)
                .doOnSuccess(value -> cacheMonitor.afterGet(key, value, CacheLevel.L1))
                .switchIfEmpty(
                        secondCache.get(key)
                                .doOnSuccess(value -> cacheMonitor.afterGet(key, value, CacheLevel.L2))
                                .filter(Objects::nonNull)
                                .doOnNext(cacheValue -> firstCache.put(key, Mono.justOrEmpty(cacheValue.getValue())))
                );
    }

    protected String toRemoteStoreKey(String localKey) {
        // Hash类型 redis（留给 RemoteCache 处理）
        if (useKeyPrefix) {
            return getName() + ":" + localKey;
        }
        return localKey;
    }

    protected String toLocalStoreKey(K key) {
        return keyConvertor.apply(key);
    }

    protected Object toLocalStoreValue(V value) {
        if (null == value) {
            if (allowNullValue) {
                return null;
            }
            throw new CacheValueNullException();
        }
        if (serializeLocalValue) {
            if (compressLocalValue) {
                return localValueCompressor.compress(localValueSerializer.serialize(value));
            }
            return localValueSerializer.serialize(value);
        }
        return value;
    }

    protected byte[] toRemoteStoreValue(V value) {
        if (null == value) {
            if (allowNullValue) {
                return NullValue.INSTANCE_BYTES;
            }
            throw new CacheValueNullException();
        }
        byte[] remoteValue = remoteValueSerializer.serialize(value);
        if (compressRemoteValue) {
            return remoteValueCompressor.compress(remoteValue);
        }
        return remoteValue;
    }

    @SuppressWarnings("unchecked")
    protected Mono<CacheValue<V>> fromLocalStoreValue(CacheValue<Object> cacheValue) {
        if (cacheValue == null) {
            return Mono.empty();
        }
        // 本地缓存需要判断是否允许空值
        if (!cacheValue.hasValue()) {
            if (allowNullValue) {
                return Mono.justOrEmpty((CacheValue<V>) cacheValue);
            }
            return Mono.empty();
        }

        Object storeValue = cacheValue.getValue();
        if (serializeLocalValue) {
            if (compressLocalValue) {
                V value = localValueSerializer.deserialize(localValueCompressor.decompress((byte[]) storeValue));
                return Mono.just(CacheValues.newCacheValue(value));
            }
            return Mono.just(CacheValues.newCacheValue(localValueSerializer.deserialize((byte[]) storeValue)));
        }
        return Mono.just((CacheValue<V>) cacheValue);
    }

    @SuppressWarnings("unchecked")
    protected Mono<CacheValue<V>> fromRemoteStoreValue(CacheValue<byte[]> cacheValue) {
        if (cacheValue == null) {
            return Mono.empty();
        }
        if (!cacheValue.hasValue()) {
            return Mono.justOrEmpty((CacheValue<V>) cacheValue);
        }
        byte[] storeValue = cacheValue.getValue();
        // 远程缓存需要判断是否是空值(NullValue)
        if (Arrays.equals(NullValue.INSTANCE_BYTES, storeValue)) {
            if (allowNullValue) {
                return Mono.just(CacheValues.emptyCacheValue());
            }
            return Mono.empty();
        }
        if (compressRemoteValue) {
            V value = remoteValueSerializer.deserialize(remoteValueCompressor.decompress(storeValue));
            return Mono.just(CacheValues.newCacheValue(value));
        }
        return Mono.just(CacheValues.newCacheValue(remoteValueSerializer.deserialize(storeValue)));
    }

    @Override
    protected Mono<CacheValue<V>> doGet(K key, CacheLoader<K, V> cacheLoader) {
        return cacheLoader.load(key)
                .doOnSuccess(value -> this.doPut(key, value))
                .doOnSuccess(value -> cacheMonitor.afterLoad(key, value))
                .map(CacheValue::new);
    }

    @Override
    protected Flux<KeyValue<K, CacheValue<V>>> doGetAll(Set<? extends K> keys) {
        Set<K> keySet = new HashSet<>(keys);
        return firstCache.getAll(keySet)
                .doOnNext(kv -> keySet.remove(kv.getKey()))
                .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), CacheLevel.L1))
                .collect(() -> new ArrayList<KeyValue<K, CacheValue<V>>>(keySet.size()), ArrayList::add)
                .filter(firstList -> CollectionUtils.isNotEmpty(keySet))
                .flatMapMany(firstList -> secondCache.getAll(keySet)
                        .doOnNext(kv -> cacheMonitor.afterGet(kv.getKey(), kv.getValue(), CacheLevel.L2))
                        .doOnNext(kv -> firstCache.put(kv.getKey(), Mono.justOrEmpty(kv.getValue().getValue())))
                        .concatWith(Flux.fromIterable(firstList))
                );
    }

    @Override
    protected Mono<Void> doPut(K key, V value) {
        return secondCache.put(key, Mono.justOrEmpty(value))
                .mergeWith(firstCache.put(key, Mono.justOrEmpty(value)))
                .then();
    }

    @Override
    protected Mono<Void> doPutAll(Map<? extends K, ? extends V> keyValues) {
        return secondCache.putAll(Mono.just(keyValues))
                .mergeWith(firstCache.putAll(Mono.just(keyValues)))
                .then();
    }

    @Override
    protected Mono<Void> doRemove(K key) {
        return secondCache.remove(key).mergeWith(firstCache.remove(key)).then();
    }

    @Override
    protected Mono<Void> doRemoveAll(Set<? extends K> keys) {
        return secondCache.removeAll(keys).mergeWith(firstCache.removeAll(keys)).then();
    }

    @Override
    public Mono<Void> clear() {
        return secondCache.clear()
                .doOnSuccess(vod -> cacheMonitor.afterClear(CacheLevel.L1))
                .mergeWith(vod -> firstCache.clear().doOnSuccess(vod2 -> cacheMonitor.afterClear(CacheLevel.L1)))
                .then();
    }

}