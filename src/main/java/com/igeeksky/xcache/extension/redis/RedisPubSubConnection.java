package com.igeeksky.xcache.extension.redis;


import reactor.core.publisher.Mono;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public interface RedisPubSubConnection extends AutoCloseable {

    Mono<Void> psubscribe(String... patterns);

    Mono<Void> punsubscribe(String... patterns);

    Mono<Void> subscribe(String... channels);

    Mono<Void> unsubscribe(String... channels);

    void addListener(RedisPubSubListener listener);

    Mono<Long> publish(String channel, byte[] message);
}
