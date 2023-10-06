package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.common.ExpiryKeyValue;
import com.igeeksky.xtool.core.io.IOUtils;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceClusterConnection extends AbstractLettuceConnection {

    private final RedisAdvancedClusterReactiveCommands<byte[], byte[]> redisReactiveCommands;

    private final RedisAdvancedClusterReactiveCommands<byte[], byte[]> bashReactiveCommands;

    private final StatefulRedisClusterConnection<byte[], byte[]> redisConnection;

    private final StatefulRedisClusterConnection<byte[], byte[]> bashRedisConnection;

    public LettuceClusterConnection(StatefulRedisClusterConnection<byte[], byte[]> redisConnection,
                                    StatefulRedisClusterConnection<byte[], byte[]> bashRedisConnection) {
        super(redisConnection.reactive(), redisConnection.reactive());
        this.redisConnection = redisConnection;
        this.bashRedisConnection = bashRedisConnection;
        this.redisReactiveCommands = redisConnection.reactive();
        this.bashReactiveCommands = bashRedisConnection.reactive();
    }

    @Override
    public boolean isCluster() {
        return true;
    }

    @Override
    public Mono<Void> mpsetex(List<ExpiryKeyValue<byte[], byte[]>> keyValues) {
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
        return redisReactiveCommands.del(keys);
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(this.redisConnection, this.bashRedisConnection);
    }

}