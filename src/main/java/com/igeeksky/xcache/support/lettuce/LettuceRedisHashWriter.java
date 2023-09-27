package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.extension.redis.RedisHashWriter;
import com.igeeksky.xcache.extension.redis.RedisOperationException;
import io.lettuce.core.cluster.api.reactive.RedisClusterReactiveCommands;
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
    private final RedisClusterReactiveCommands<byte[], byte[]> redisReactiveCommands;

    public LettuceRedisHashWriter(LettuceConnectionFactory factory) {
        this.cluster = factory.isCluster();
        if (isCluster()) {
            this.redisReactiveCommands = factory.getClusterReactiveCommands();
        } else {
            this.redisReactiveCommands = factory.getRedisReactiveCommands();
        }
    }

    @Override
    public Mono<byte[]> hget(byte[] key, byte[] field) {
        return redisReactiveCommands.hget(key, field);
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> hmget(byte[] key, byte[]... fields) {
        return redisReactiveCommands.hmget(key, fields)
                .map(kv -> new KeyValue<>(kv.getKey(), kv.getValue()));
    }

    @Override
    public Mono<Boolean> hset(byte[] key, byte[] field, byte[] value) {
        return redisReactiveCommands.hset(key, field, value);
    }

    @Override
    public Mono<Void> hmset(byte[] key, Map<byte[], byte[]> map) {
        return redisReactiveCommands.hmset(key, map).flatMap(result -> {
            if (!Objects.equals(OK, result)) {
                String errMsg = String.format("redis hmset error. key:[%s]", new String(key));
                return Mono.error(new RedisOperationException(errMsg));
            }
            return Mono.empty();
        }).then();
    }

    @Override
    public Mono<Long> hdel(byte[] key, byte[]... fields) {
        return redisReactiveCommands.hdel(key, fields);
    }

    @Override
    public Mono<Long> del(byte[]... keys) {
        return redisReactiveCommands.del(keys);
    }

    @Override
    public boolean isCluster() {
        return cluster;
    }

}