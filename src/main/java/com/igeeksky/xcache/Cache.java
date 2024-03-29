package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.extension.loader.CacheLoader;
import reactor.core.publisher.Mono;

/**
 * <p>缓存</p>
 *
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-05
 */
public interface Cache<K, V> extends ReactiveCache<K, V> {

    String getName();

    Class<K> getKeyType();

    Class<V> getValueType();

    Mono<CacheValue<V>> get(K key, CacheLoader<K, V> cacheLoader);

    SyncCache<K, V> sync();

    AsyncCache<K, V> async();

}