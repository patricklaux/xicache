package com.igeeksky.xcache.extension.redis;

import reactor.core.publisher.Mono;

/**
 * Redis命令接口
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-03
 */
public interface RedisWriter extends AutoCloseable {

    String OK = "OK";

    Mono<Void> reactiveClose();

    boolean isCluster();

    @Override
    default void close() {
        reactiveClose().subscribe();
    }
}
