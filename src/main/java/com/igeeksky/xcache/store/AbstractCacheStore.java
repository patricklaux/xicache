package com.igeeksky.xcache.store;


import com.igeeksky.xcache.CacheStore;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.CacheValueNullException;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xtool.core.collection.Maps;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 缓存存储器
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-07-29
 */
public abstract class AbstractCacheStore<K, V> implements CacheStore<K, V> {

    protected final boolean enableNullValue;

    public AbstractCacheStore(boolean enableNullValue) {
        this.enableNullValue = enableNullValue;
    }

    @Override
    public Flux<KeyValue<K, CacheValue<V>>> getAll(Set<? extends K> keys) {
        return doGetAll(keys).filter(KeyValue::hasValue);
    }

    protected abstract Flux<KeyValue<K, CacheValue<V>>> doGetAll(Set<? extends K> keys);

    @Override
    public Mono<Void> putAll(Mono<Map<? extends K, ? extends V>> keyValues) {
        return keyValues.map(kvs -> {
            Map<K, V> map = new HashMap<>(kvs.size());
            Set<? extends Map.Entry<? extends K, ? extends V>> entrySet = kvs.entrySet();
            for (Map.Entry<? extends K, ? extends V> entry : entrySet) {
                K key = entry.getKey();
                V value = entry.getValue();
                if (null == value && !enableNullValue) {
                    throw new CacheValueNullException();
                }
                map.put(key, value);
            }
            return map;
        }).filter(Maps::isNotEmpty).flatMap(this::doPutAll);
    }

    protected abstract Mono<Void> doPutAll(Map<? extends K, ? extends V> keyValues);

}
