package com.igeeksky.xcache.common;

import reactor.core.publisher.Mono;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-05
 */
@FunctionalInterface
public interface CacheLoader<K, V> {

    Mono<V> load(K key);

}
