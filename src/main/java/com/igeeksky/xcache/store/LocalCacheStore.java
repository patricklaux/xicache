package com.igeeksky.xcache.store;

import com.igeeksky.xcache.common.CacheValue;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-16
 */
public interface LocalCacheStore extends CacheStore<String, Object> {

    @Override
    default Mono<Void> putAll(Mono<Map<? extends String, ?>> keyValues) {
        return this.doPutAll(
                keyValues.map(kvs -> {
                    Map<String, CacheValue<Object>> newMap = new LinkedHashMap<>();
                    kvs.forEach((k, v) -> newMap.put(k, new CacheValue<>(v)));
                    return newMap;
                })
        );
    }

    Mono<Void> doPutAll(Mono<Map<String, CacheValue<Object>>> keyValues);

    @Override
    default Mono<Void> put(String key, Mono<Object> value) {
        return doPut(key, value.map(CacheValue::new));
    }

    Mono<Void> doPut(String key, Mono<CacheValue<Object>> value);
}
