package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.ExpiryKeyValue;
import com.igeeksky.xtool.core.io.IOUtils;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceConnection extends AbstractLettuceConnection {

    private final RedisReactiveCommands<byte[], byte[]> redisReactiveCommands;

    private final RedisReactiveCommands<byte[], byte[]> bashReactiveCommands;

    private final StatefulRedisConnection<byte[], byte[]> redisConnection;

    private final StatefulRedisConnection<byte[], byte[]> bashRedisConnection;

    public LettuceConnection(StatefulRedisConnection<byte[], byte[]> redisConnection,
                             StatefulRedisConnection<byte[], byte[]> bashRedisConnection) {
        super(redisConnection.reactive(), redisConnection.reactive());
        this.redisConnection = redisConnection;
        this.bashRedisConnection = bashRedisConnection;
        this.redisReactiveCommands = redisConnection.reactive();
        this.bashReactiveCommands = bashRedisConnection.reactive();
    }

    @Override
    public boolean isCluster() {
        return false;
    }

    @Override
    public Mono<Void> mpsetex(List<ExpiryKeyValue<byte[], byte[]>> keyValues) {
        return Flux.fromIterable(keyValues)
                .flatMap(kv -> bashReactiveCommands
                        .psetex(kv.getKey(), kv.getTtl(), kv.getValue())
                        .doOnNext(result -> isSetSuccess(kv.getKey(), kv.getValue(), result))
                )
                .then()
                .doOnSuccess(vod -> bashRedisConnection.flushCommands());
    }

    @Override
    public Mono<Long> del(byte[]... keys) {
        return redisReactiveCommands.del(keys);
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(this.redisConnection, this.bashRedisConnection);
    }

}
