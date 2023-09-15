package com.igeeksky.xcache.store;

import com.igeeksky.xcache.common.*;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.Compressor;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.serializer.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-11
 */
public abstract class AbstractLocalCacheStore<K, V> extends AbstractCacheStore<K, V> {

    private final KeyConvertor keyConvertor;

    private final boolean enableSerializeValue;

    private final Serializer<V> valueSerializer;

    private final boolean enableCompressValue;

    private final Compressor compressor;

    public AbstractLocalCacheStore(CacheConfig<K, V> config) {
        super(config.isEnableNullValue());
        this.keyConvertor = config.getKeyConvertor();
        this.compressor = config.getCompressor();
        this.valueSerializer = config.getValueSerializer();
        this.enableCompressValue = config.isEnableCompressValue();
        this.enableSerializeValue = config.isEnableSerializeValue();
    }

    protected Object toStoreKey(K key) {
        if (keyConvertor == null) {
            return key;
        }
        return keyConvertor.apply(key);
    }

    @Override
    public Mono<CacheValue<V>> get(K key) {
        return Mono.just(key).map(this::toStoreKey).flatMap(k -> this.fromStoreValue(doStoreGet(k)));
    }

    @Override
    public Flux<KeyValue<K, CacheValue<V>>> doGetAll(Set<? extends K> keys) {
        return Flux.fromIterable(keys)
                .flatMap(key -> this.fromStoreValue(doStoreGet(toStoreKey(key)))
                        .map(v -> new KeyValue<>(key, v)));
    }

    @Override
    public Mono<Void> put(K key, Mono<V> mono) {
        return mono.doOnNext(v -> doStorePut(toStoreKey(key), toStoreValue(v))).then();
    }

    @Override
    public Mono<Void> doPutAll(Map<? extends K, ? extends V> keyValues) {
        return Mono.just(keyValues)
                .map(kvs -> {
                    Map<Object, CacheValue<Object>> newMap = new LinkedHashMap<>();
                    kvs.forEach((k, v) -> newMap.put(toStoreKey(k), toStoreValue(v)));
                    return newMap;
                })
                .doOnNext(this::doStorePutAll)
                .then();
    }

    @Override
    public Mono<Void> remove(K key) {
        return Mono.just(key).map(this::toStoreKey).doOnNext(this::doStoreRemove).then();
    }

    @Override
    public Mono<Void> removeAll(Set<? extends K> keys) {
        return Mono.just(keys)
                .map(ks -> {
                    Set<Object> set = new LinkedHashSet<>(keys.size());
                    keys.forEach(k -> set.add(toStoreKey(k)));
                    return set;
                })
                .doOnNext(this::doStoreRemoveAll).then();
    }

    protected abstract Mono<CacheValue<Object>> doStoreGet(Object key);

    protected abstract void doStorePut(Object key, CacheValue<Object> cacheValue);

    protected abstract void doStorePutAll(Map<Object, CacheValue<Object>> keyValues);

    protected abstract void doStoreRemove(Object key);

    protected abstract void doStoreRemoveAll(Set<Object> keys);

    /**
     * 转换存储对象 为 源对象
     *
     * @param mono 缓存中的存储对象
     * @return 返回的源对象（如果可压缩，先解压缩；如果可序列化，先反序列化）。
     */
    @SuppressWarnings("unchecked")
    private Mono<CacheValue<V>> fromStoreValue(Mono<CacheValue<Object>> mono) {
        return mono.filter(Objects::nonNull)
                .filter(cv -> cv.getValue() != null || enableNullValue)
                .map(cv -> {
                    Object storeValue = cv.getValue();
                    if (storeValue == null) {
                        return (CacheValue<V>) cv;
                    }
                    if (enableSerializeValue) {
                        if (enableCompressValue) {
                            return CacheValues.newCacheValue(valueSerializer.deserialize(compressor.decompress((byte[]) storeValue)));
                        }
                        return CacheValues.newCacheValue(valueSerializer.deserialize((byte[]) storeValue));
                    }
                    return (CacheValue<V>) cv;
                });
    }

    /**
     * 转换源对象 为 存储对象
     *
     * @param value 源对象，允许为空
     * @return 如果可序列化，返回序列化数据；如果可压缩，返回压缩数据；其它返回源对象。
     */
    private CacheValue<Object> toStoreValue(V value) {
        if (null == value) {
            if (enableNullValue) {
                return CacheValues.emptyCacheValue();
            }
            throw new CacheValueNullException();
        }
        if (enableSerializeValue) {
            if (enableCompressValue) {
                return CacheValues.newCacheValue(compressor.compress(valueSerializer.serialize(value)));
            }
            return CacheValues.newCacheValue(valueSerializer.serialize(value));
        }
        return CacheValues.newCacheValue(value);
    }

}
