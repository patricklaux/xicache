package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.ExpiryKeyValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.extension.redis.RedisOperationException;
import com.igeeksky.xcache.extension.redis.RedisStringWriter;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.reactive.RedisKeyReactiveCommands;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
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

    private final StatefulConnection<byte[], byte[]> bashStatefulConnection;

    private final RedisStringReactiveCommands<byte[], byte[]> bashReactiveCommands;

    private final StatefulConnection<byte[], byte[]> statefulConnection;

    private final RedisKeyReactiveCommands<byte[], byte[]> keyReactiveCommands;

    private final RedisStringReactiveCommands<byte[], byte[]> stringReactiveCommands;

    public LettuceRedisStringWriter(LettuceConnectionManager connectionManager) {
        this.cluster = connectionManager.isCluster();
        this.statefulConnection = connectionManager.getStatefulConnection();
        this.stringReactiveCommands = connectionManager.getStringReactiveCommands();
        this.bashStatefulConnection = connectionManager.getBashStatefulConnection();
        this.bashReactiveCommands = connectionManager.getBashStringReactiveCommands();
        this.keyReactiveCommands = connectionManager.getKeyReactiveCommands();
    }

    @Override
    public Mono<byte[]> get(byte[] key) {
        return stringReactiveCommands.get(key);
    }

    @Override
    public Flux<KeyValue<byte[], byte[]>> mget(byte[]... keys) {
        return stringReactiveCommands.mget(keys)
                .map(kv -> new KeyValue<>(kv.getKey(), kv.getValue()));
    }

    @Override
    public Mono<Void> set(byte[] key, byte[] value) {
        return stringReactiveCommands.set(key, value).doOnNext(result -> isSetSuccess(key, value, result)).then();
    }

    @Override
    public Mono<Void> psetex(byte[] key, long milliseconds, byte[] value) {
        return stringReactiveCommands.psetex(key, milliseconds, value)
                .doOnNext(result -> isSetSuccess(key, value, result))
                .then();
    }

    @Override
    public Mono<Void> mset(Map<byte[], byte[]> keyValues) {
        return stringReactiveCommands.mset(keyValues).then();
    }

    @Override
    public final Mono<Void> mpsetex(List<ExpiryKeyValue<byte[], byte[]>> keyValues) {
        return Flux.fromIterable(keyValues)
                .flatMap(kv -> bashReactiveCommands
                        .psetex(kv.getKey(), kv.getTtl(), kv.getValue())
                        .doOnNext(result -> isSetSuccess(kv.getKey(), kv.getValue(), result))
                )
                .then()
                .doOnSuccess(vod -> bashStatefulConnection.flushCommands());
    }

    @Override
    public Mono<Long> del(byte[]... keys) {
        return keyReactiveCommands.del(keys);
    }

    @Override
    public Mono<Void> reactiveClose() {
        return Mono.fromFuture(statefulConnection.closeAsync().thenCompose(vod -> bashStatefulConnection.closeAsync()));
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
