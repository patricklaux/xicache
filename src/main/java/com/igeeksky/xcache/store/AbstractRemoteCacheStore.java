package com.igeeksky.xcache.store;


import com.igeeksky.xcache.NullValue;
import com.igeeksky.xcache.common.*;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.Compressor;
import com.igeeksky.xcache.extension.serializer.Serializer;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import com.igeeksky.xtool.core.lang.ArrayUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-22
 */
public abstract class AbstractRemoteCacheStore<K, V> extends AbstractCacheStore<K, V> {

    private final boolean enableCompressValue;
    private final Compressor valueCompressor;
    protected final Serializer<V> valueSerializer;

    public AbstractRemoteCacheStore(CacheConfig<K, V> config, Serializer<V> valueSerializer, Compressor valueCompressor) {
        super(config.isEnableNullValue());
        this.valueSerializer = valueSerializer;
        this.valueCompressor = valueCompressor;
        this.enableCompressValue = config.isEnableCompressValue();
    }

    @Override
    public Mono<CacheValue<V>> get(K key) {
        byte[] keyBytes = toStoreKey(key);
        if (ArrayUtils.isNotEmpty(keyBytes)) {
            return doStoreGet(keyBytes);
        }
        return Mono.error(new RuntimeException("Key bytes must not be null or empty."));
    }

    protected abstract Mono<CacheValue<V>> doStoreGet(byte[] keyBytes);

    @Override
    public Flux<KeyValue<K, CacheValue<V>>> doGetAll(Set<? extends K> keys) {
        byte[][] keyArray = new byte[keys.size()][];
        int i = 0;
        for (K key : keys) {
            keyArray[i] = toStoreKey(key);
            i++;
        }
        return this.doStoreGetAll(keyArray);
    }

    protected abstract Flux<KeyValue<K, CacheValue<V>>> doStoreGetAll(byte[][] keys);

    @Override
    public Mono<Void> put(K key, Mono<V> mono) {
        return mono.doOnNext(value -> doStorePut(toStoreKey(key), toStoreValue(value))).then();
    }

    protected abstract void doStorePut(byte[] key, byte[] value);

    @Override
    public Mono<Void> doPutAll(Map<? extends K, ? extends V> keyValues) {
        Map<byte[], byte[]> keyValuesMap = new HashMap<>(keyValues.size());
        for (Map.Entry<? extends K, ? extends V> entry : keyValues.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            byte[] keyBytes = toStoreKey(key);
            byte[] valueBytes = toStoreValue(value);
            if (null != keyBytes && null != valueBytes) {
                keyValuesMap.put(keyBytes, valueBytes);
            }
        }
        return this.doStorePutAll(keyValuesMap);
    }

    protected abstract Mono<Void> doStorePutAll(Map<byte[], byte[]> keyValues);

    @Override
    public Mono<Void> remove(K key) {
        return Mono.just(key)
                .filter(Objects::nonNull)
                .map(this::toStoreKey)
                .filter(Objects::nonNull)
                .flatMap(this::doStoreRemove);
    }

    protected abstract Mono<Void> doStoreRemove(byte[] key);

    @Override
    public Mono<Void> removeAll(Set<? extends K> keys) {
        return Mono.justOrEmpty(keys)
                .filter(CollectionUtils::isNotEmpty)
                .map(ks -> {
                    List<byte[]> list = new ArrayList<>(ks.size());
                    for (K k : ks) {
                        byte[] keyBytes = toStoreKey(k);
                        if (null != keyBytes) {
                            list.add(keyBytes);
                        }
                    }
                    return list.toArray(new byte[list.size()][]);
                })
                .filter(bytes -> bytes.length > 0)
                .flatMap(this::doStoreRemoveAll);
    }

    protected abstract Mono<Void> doStoreRemoveAll(byte[][] keys);

    protected abstract byte[] toStoreKey(K key);

    protected abstract K fromStoreKey(byte[] key);

    /**
     * 存储对象 转换为 源对象
     *
     * @param storeValue 存储对象，可能为空。
     * @return <p>返回反序列化后的 源对象。如果开启压缩，解压缩后再反序列化。</p>
     * <p>如果允许存储空对象，且字节数组为 {@link NullValue#INSTANCE_BYTES}，返回 {@link CacheValues#emptyCacheValue()}</p>
     * <p>如果不允许存储空对象，而字节数组为 {@link NullValue#INSTANCE_BYTES}，返回 null</p>
     */
    protected CacheValue<V> fromStoreValue(byte[] storeValue) {
        if (null == storeValue) {
            return null;
        }

        // 远程缓存需要判断是否是 空值(NullValue)
        if (Arrays.equals(NullValue.INSTANCE_BYTES, storeValue)) {
            if (enableNullValue) {
                return CacheValues.emptyCacheValue();
            }
            return null;
        }

        if (enableCompressValue) {
            return CacheValues.newCacheValue(valueSerializer.deserialize(valueCompressor.decompress(storeValue)));
        }
        return CacheValues.newCacheValue(valueSerializer.deserialize(storeValue));
    }

    /**
     * 源对象 转换为 存储对象
     *
     * @param value 源对象，允许为空
     * @return <p>返回存储对象。如果开启压缩，返回先序列化再压缩的存储对象；其它返回序列化的存储对象。</p>
     * 如果 源对象 为空，返回 {@link NullValue#INSTANCE_BYTES}
     */
    private byte[] toStoreValue(V value) {
        if (null == value) {
            if (enableNullValue) {
                return NullValue.INSTANCE_BYTES;
            }
            throw new CacheValueNullException();
        }

        byte[] storeValue = valueSerializer.serialize(value);
        if (enableCompressValue) {
            return valueCompressor.compress(storeValue);
        }
        return storeValue;
    }

}
