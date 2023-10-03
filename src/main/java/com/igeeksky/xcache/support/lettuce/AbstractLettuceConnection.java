package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.extension.redis.RedisConnection;
import com.igeeksky.xcache.extension.redis.RedisOperationException;
import io.lettuce.core.api.reactive.RedisHashReactiveCommands;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-03
 */
public abstract class AbstractLettuceConnection implements RedisConnection {

    private final RedisStringReactiveCommands<byte[], byte[]> stringCommands;
    private final RedisHashReactiveCommands<byte[], byte[]> hashCommands;

    public AbstractLettuceConnection(RedisStringReactiveCommands<byte[], byte[]> stringCommands,
                                     RedisHashReactiveCommands<byte[], byte[]> hashCommands) {
        this.stringCommands = stringCommands;
        this.hashCommands = hashCommands;
    }

    @Override
    public Mono<byte[]> get(byte[] key) {
        return stringCommands.get(key);
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> mget(byte[]... keys) {
        return stringCommands.mget(keys)
                .map(kv -> new KeyValue<>(kv.getKey(), kv.getValue()));
    }

    @Override
    public Mono<Void> set(byte[] key, byte[] value) {
        return stringCommands.set(key, value).doOnNext(result -> isSetSuccess(key, value, result)).then();
    }

    @Override
    public Mono<Void> psetex(byte[] key, long milliseconds, byte[] value) {
        return stringCommands.psetex(key, milliseconds, value)
                .doOnNext(result -> isSetSuccess(key, value, result))
                .then();
    }

    @Override
    public Mono<Void> mset(Map<byte[], byte[]> keyValues) {
        return stringCommands.mset(keyValues).then();
    }

    @Override
    public Mono<byte[]> hget(byte[] key, byte[] field) {
        return hashCommands.hget(key, field);
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> hmget(byte[] key, byte[]... fields) {
        return hashCommands.hmget(key, fields)
                .map(kv -> new KeyValue<>(kv.getKey(), kv.getValue()));
    }

    @Override
    public Mono<Boolean> hset(byte[] key, byte[] field, byte[] value) {
        return hashCommands.hset(key, field, value);
    }

    @Override
    public Mono<Void> hmset(byte[] key, Map<byte[], byte[]> map) {
        return hashCommands.hmset(key, map).flatMap(result -> {
            if (!Objects.equals(OK, result)) {
                String errMsg = String.format("redis hmset error. key:[%s]", new String(key));
                return Mono.error(new RedisOperationException(errMsg));
            }
            return Mono.empty();
        }).then();
    }

    @Override
    public Mono<Long> hdel(byte[] key, byte[]... fields) {
        return hashCommands.hdel(key, fields);
    }

    protected void isSetSuccess(byte[] key, byte[] value, String result) {
        if (!Objects.equals(OK, result)) {
            String errMsg = String.format("redis set error. key:[%s] value:[%s]", new String(key), new String(value));
            throw new RedisOperationException(errMsg);
        }
    }
}
