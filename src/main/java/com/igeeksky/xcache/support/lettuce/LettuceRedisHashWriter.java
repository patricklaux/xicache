package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.extension.redis.RedisHashWriter;
import com.igeeksky.xcache.extension.redis.RedisOperationException;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.reactive.RedisHashReactiveCommands;
import io.lettuce.core.api.reactive.RedisKeyReactiveCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-12
 */
public class LettuceRedisHashWriter implements RedisHashWriter {

    private final boolean cluster;

    private final StatefulConnection<byte[], byte[]> statefulConnection;

    private final RedisKeyReactiveCommands<byte[], byte[]> keyReactiveCommands;

    private final RedisHashReactiveCommands<byte[], byte[]> hashReactiveCommands;

    public LettuceRedisHashWriter(LettuceConnectionManager connectionManager) {
        this.cluster = connectionManager.isCluster();
        this.statefulConnection = connectionManager.getStatefulConnection();
        this.keyReactiveCommands = connectionManager.getKeyReactiveCommands();
        this.hashReactiveCommands = connectionManager.getHashReactiveCommands();
    }

    @Override
    public Mono<byte[]> hget(byte[] key, byte[] field) {
        return hashReactiveCommands.hget(key, field);
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> hmget(byte[] key, byte[]... fields) {
        return hashReactiveCommands.hmget(key, fields)
                .map(kv -> new KeyValue<>(kv.getKey(), kv.getValue()));
    }

    @Override
    public Mono<Boolean> hset(byte[] key, byte[] field, byte[] value) {
        return hashReactiveCommands.hset(key, field, value);
    }

    @Override
    public Mono<Void> hmset(byte[] key, Map<byte[], byte[]> map) {
        return hashReactiveCommands.hmset(key, map).flatMap(result -> {
            if (!Objects.equals(OK, result)) {
                String errMsg = String.format("redis hmset error. key:[%s]", new String(key));
                return Mono.error(new RedisOperationException(errMsg));
            }
            return Mono.empty();
        }).then();
    }

    @Override
    public Mono<Long> hdel(byte[] key, byte[]... fields) {
        return hashReactiveCommands.hdel(key, fields);
    }

    @Override
    public Mono<Long> del(byte[]... keys) {
        return keyReactiveCommands.del(keys);
    }

    @Override
    public Mono<Void> reactiveClose() {
        return Mono.fromFuture(statefulConnection.closeAsync());
    }

    @Override
    public boolean isCluster() {
        return cluster;
    }
}