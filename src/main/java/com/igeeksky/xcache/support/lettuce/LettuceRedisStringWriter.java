package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.ExpiryKeyValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.extension.redis.RedisOperationException;
import com.igeeksky.xcache.extension.redis.RedisStringWriter;
import io.lettuce.core.cluster.api.reactive.RedisClusterReactiveCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-12
 */
public class LettuceRedisStringWriter implements RedisStringWriter {

    private final boolean cluster;

    private final RedisClusterReactiveCommands<byte[], byte[]> reactiveCommands;
    private final RedisClusterReactiveCommands<byte[], byte[]> bashReactiveCommands;

    public LettuceRedisStringWriter(LettuceConnectionFactory connectionManager) {
        this.cluster = connectionManager.isCluster();
        if (isCluster()) {
            this.reactiveCommands = connectionManager.getClusterReactiveCommands();
            this.bashReactiveCommands = connectionManager.getBashClusterReactiveCommands();
        } else {
            this.reactiveCommands = connectionManager.getRedisReactiveCommands();
            this.bashReactiveCommands = connectionManager.getBashRedisReactiveCommands();
        }
    }

    @Override
    public Mono<byte[]> get(byte[] key) {
        return reactiveCommands.get(key);
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> mget(byte[]... keys) {
        return reactiveCommands.mget(keys)
                .map(kv -> new KeyValue<>(kv.getKey(), kv.getValue()));
    }

    @Override
    public Mono<Void> set(byte[] key, byte[] value) {
        return reactiveCommands.set(key, value).doOnNext(result -> isSetSuccess(key, value, result)).then();
    }

    @Override
    public Mono<Void> psetex(byte[] key, long milliseconds, byte[] value) {
        return reactiveCommands.psetex(key, milliseconds, value)
                .doOnNext(result -> isSetSuccess(key, value, result))
                .then();
    }

    @Override
    public Mono<Void> mset(Map<byte[], byte[]> keyValues) {
        return reactiveCommands.mset(keyValues).then();
    }

    @Override
    public final Mono<Void> mpsetex(List<ExpiryKeyValue<byte[], byte[]>> keyValues) {
        return Flux.fromIterable(keyValues)
                .flatMap(kv -> bashReactiveCommands
                        .psetex(kv.getKey(), kv.getTtl(), kv.getValue())
                        .doOnNext(result -> isSetSuccess(kv.getKey(), kv.getValue(), result))
                )
                .then()
                .doOnSuccess(vod -> bashReactiveCommands.flushCommands());
    }

    @Override
    public Mono<Long> del(byte[]... keys) {
        return reactiveCommands.del(keys);
    }

    @Override
    public boolean isCluster() {
        return cluster;
    }

    private void isSetSuccess(byte[] key, byte[] value, String result) {
        if (!Objects.equals(OK, result)) {
            String errMsg = String.format("redis set error. key:[%s] value:[%s]", new String(key), new String(value));
            throw new RedisOperationException(errMsg);
        }
    }

}
