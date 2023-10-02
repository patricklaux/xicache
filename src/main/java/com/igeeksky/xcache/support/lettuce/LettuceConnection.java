package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.ExpiryKeyValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.extension.redis.RedisConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceConnection implements RedisConnection {

    private final RedisReactiveCommands<byte[], byte[]> redisReactiveCommands;

    private final RedisReactiveCommands<byte[], byte[]> bashRedisReactiveCommands;

    private final StatefulRedisConnection<byte[], byte[]> redisConnection;

    private final StatefulRedisConnection<byte[], byte[]> bashRedisConnection;

    public LettuceConnection(StatefulRedisConnection<byte[], byte[]> redisConnection,
                             StatefulRedisConnection<byte[], byte[]> bashRedisConnection) {
        this.redisConnection = redisConnection;
        this.bashRedisConnection = bashRedisConnection;
        this.redisReactiveCommands = redisConnection.reactive();
        this.bashRedisReactiveCommands = bashRedisConnection.reactive();
    }

    @Override
    public boolean isCluster() {
        return false;
    }

    @Override
    public Mono<byte[]> get(byte[] key) {
        return redisReactiveCommands.get(key);
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> mget(byte[]... keys) {
        return null;
    }

    @Override
    public Mono<Void> set(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Mono<Void> psetex(byte[] key, long milliseconds, byte[] value) {
        return null;
    }

    @Override
    public Mono<Void> mset(Map<byte[], byte[]> keyValues) {
        return null;
    }

    @Override
    public Mono<Void> mpsetex(List<ExpiryKeyValue<byte[], byte[]>> keyValues) {
        return null;
    }

    @Override
    public Mono<Long> del(byte[]... keys) {
        return null;
    }

    @Override
    public Mono<byte[]> hget(byte[] key, byte[] field) {
        return null;
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> hmget(byte[] key, byte[]... field) {
        return null;
    }

    @Override
    public Mono<Boolean> hset(byte[] key, byte[] field, byte[] value) {
        return null;
    }

    @Override
    public Mono<Void> hmset(byte[] key, Map<byte[], byte[]> map) {
        return null;
    }

    @Override
    public Mono<Long> hdel(byte[] key, byte[]... fields) {
        return null;
    }
}
