package com.igeeksky.xcache;

import com.igeeksky.xcache.common.*;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.Compressor;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.serializer.Serializer;
import com.igeeksky.xtool.core.collection.Maps;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-19
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    private final String name;
    private final Class<K> keyType;
    private final Class<V> valueType;

    // TODO 设置属性
    private KeyConvertor keyConvertor;

    private Serializer<V> localValueSerializer;

    private Compressor localValueCompressor;

    private Serializer<V> remoteValueSerializer;

    private Compressor remoteValueCompressor;

    private boolean serializeLocalValue;

    private boolean compressLocalValue;

    private boolean compressRemoteValue;

    private final boolean useKeyPrefix = true;

    private boolean allowNullValue;

    private final Object lock = new Object();
    private volatile SyncCache<K, V> syncCache;
    private volatile AsyncCache<K, V> asyncCache;

    private final CacheLock<K> cacheLock;
    private final ContainsPredicate<K> containsPredicate;

    public AbstractCache(CacheConfig<K, V> config) {
        this.name = config.getName();
        this.keyType = config.getKeyType();
        this.valueType = config.getValueType();
        this.cacheLock = config.getCacheLock();
        this.containsPredicate = config.getContainsPredicate();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<K> getKeyType() {
        return keyType;
    }

    @Override
    public Class<V> getValueType() {
        return valueType;
    }

    @Override
    public Mono<CacheValue<V>> get(K key) {
        if (null == key) {
            return Mono.error(new CacheKeyNullException());
        }
        return this.doGet(toStoreKey(key));
    }

    protected abstract Mono<CacheValue<V>> doGet(String key);

    @Override
    public Mono<CacheValue<V>> get(K key, CacheLoader<K, V> cacheLoader) {
        return this.get(key).switchIfEmpty(loadWithLock(key, cacheLoader));
    }

    private Mono<CacheValue<V>> loadWithLock(K key, CacheLoader<K, V> cacheLoader) {
        if (containsPredicate.test(getName(), key)) {
            Lock keyLock = cacheLock.get(key);
            return Mono.just(key)
                    .doOnNext(k -> keyLock.lock())
                    .flatMap(this::get)
                    .switchIfEmpty(this.doGet(key, toStoreKey(key), cacheLoader))
                    .doFinally(s -> keyLock.unlock());
        }
        return Mono.empty();
    }

    protected abstract Mono<CacheValue<V>> doGet(K key, String storeKey, CacheLoader<K, V> cacheLoader);

    @Override
    public Flux<KeyValue<K, CacheValue<V>>> getAll(Set<? extends K> keys) {
        if (null == keys) {
            return Flux.error(new CacheKeyNullException("keys must not be null."));
        }
        if (keys.isEmpty()) {
            return Flux.empty();
        }
        Mono<Map<String, K>> mono = Mono.just(keys)
                .map(ks -> {
                    Map<String, K> map = new LinkedHashMap<>(ks.size());
                    ks.forEach(k -> {
                        if (k == null) {
                            throw new CacheKeyNullException();
                        }
                        map.put(toStoreKey(k), k);
                    });
                    return map;
                });
        return mono.flatMapMany(map ->
                this.doGetAll(new LinkedHashSet<>(map.keySet()))
                        .filter(KeyValue::hasValue)
                        .map(kv -> new KeyValue<>(map.get(kv.getKey()), kv.getValue()))
        );
    }

    protected abstract Flux<KeyValue<String, CacheValue<V>>> doGetAll(Set<String> keys);

    @Override
    public Mono<Void> putAll(Mono<Map<? extends K, ? extends V>> keyValues) {
        return keyValues
                .filter(Maps::isNotEmpty)
                .map(kvs -> {
                    Map<String, V> map = new LinkedHashMap<>(kvs.size());
                    kvs.forEach((k, v) -> {
                        if (k == null) {
                            throw new CacheKeyNullException();
                        }
                        map.put(toStoreKey(k), v);
                    });
                    return map;
                })
                .flatMap(this::doPutAll);
    }

    protected abstract Mono<Void> doPutAll(Map<String, ? extends V> keyValues);

    @Override
    public Mono<Void> put(K key, Mono<V> monoValue) {
        if (null == key) {
            return Mono.error(new CacheKeyNullException());
        }
        return monoValue
                .flatMap(value -> this.doPut(toStoreKey(key), value));
    }

    protected abstract Mono<Void> doPut(String key, V value);

    @Override
    public Mono<Void> remove(K key) {
        if (null == key) {
            return Mono.error(new CacheKeyNullException());
        }
        return this.doRemove(toStoreKey(key));
    }

    protected abstract Mono<Void> doRemove(String key);

    @Override
    public Mono<Void> removeAll(Set<? extends K> keys) {
        if (null == keys) {
            return Mono.error(new CacheKeyNullException());
        }
        if (keys.isEmpty()) {
            return Mono.empty();
        }
        return Mono.just(keys)
                .map(ks -> {
                    Set<String> set = new LinkedHashSet<>(ks.size());
                    ks.forEach(k -> {
                        if (k == null) {
                            throw new CacheKeyNullException();
                        }
                        set.add(toStoreKey(k));
                    });
                    return set;
                })
                .flatMap(this::doRemoveAll);
    }

    protected abstract Mono<Void> doRemoveAll(Set<String> keys);

    @Override
    public SyncCache<K, V> sync() {
        if (null == syncCache) {
            synchronized (lock) {
                if (null == syncCache) {
                    this.syncCache = new SyncCache.SyncCacheView<>(this);
                }
            }
        }
        return syncCache;
    }

    @Override
    public AsyncCache<K, V> async() {
        if (null == asyncCache) {
            synchronized (lock) {
                if (null == asyncCache) {
                    this.asyncCache = new AsyncCache.AsyncCacheView<>(this);
                }
            }
        }
        return asyncCache;
    }


    protected String toStoreKey(K key) {
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

}
