package com.igeeksky.xcache.support.lettuce;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisHashReactiveCommands;
import io.lettuce.core.api.reactive.RedisKeyReactiveCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisClusterReactiveCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-17
 */
public class LettuceConnectionManager implements AutoCloseable {

    private final ReentrantLock lock = new ReentrantLock();

    private final boolean cluster;

    private final Charset charset;

    private final AbstractRedisClient abstractRedisClient;

    private final StatefulRedisConnection<byte[], byte[]> redisConnection;

    private final StatefulRedisConnection<byte[], byte[]> bashRedisConnection;

    private final StatefulRedisClusterConnection<byte[], byte[]> clusterConnection;

    private final StatefulRedisClusterConnection<byte[], byte[]> bashClusterConnection;

    private final RedisReactiveCommands<byte[], byte[]> redisReactiveCommands;

    private final RedisReactiveCommands<byte[], byte[]> bashRedisReactiveCommands;

    private final RedisClusterReactiveCommands<byte[], byte[]> clusterReactiveCommands;

    private final RedisClusterReactiveCommands<byte[], byte[]> bashClusterReactiveCommands;

    private volatile StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;

    public LettuceConnectionManager(AbstractRedisClient abstractRedisClient, Charset charset) {
        this.charset = charset;
        this.abstractRedisClient = abstractRedisClient;
        if (abstractRedisClient instanceof RedisClusterClient) {
            this.cluster = true;
            RedisClusterClient redisClusterClient = (RedisClusterClient) abstractRedisClient;
            this.clusterConnection = redisClusterClient.connect(ByteArrayCodec.INSTANCE);
            this.clusterReactiveCommands = this.clusterConnection.reactive();
            this.bashClusterConnection = redisClusterClient.connect(ByteArrayCodec.INSTANCE);
            this.bashClusterConnection.setAutoFlushCommands(false);
            this.bashClusterReactiveCommands = bashClusterConnection.reactive();
            this.redisConnection = null;
            this.redisReactiveCommands = null;
            this.bashRedisConnection = null;
            this.bashRedisReactiveCommands = null;
        } else {
            this.cluster = false;
            RedisClient redisClient = (RedisClient) abstractRedisClient;
            this.redisConnection = redisClient.connect(ByteArrayCodec.INSTANCE);
            this.redisReactiveCommands = this.redisConnection.reactive();
            this.bashRedisConnection = redisClient.connect(ByteArrayCodec.INSTANCE);
            this.bashRedisConnection.setAutoFlushCommands(false);
            this.bashRedisReactiveCommands = this.bashRedisConnection.reactive();
            this.clusterConnection = null;
            this.clusterReactiveCommands = null;
            this.bashClusterConnection = null;
            this.bashClusterReactiveCommands = null;
        }
    }

    public boolean isCluster() {
        return cluster;
    }

    public StatefulConnection<byte[], byte[]> getBashStatefulConnection() {
        if (bashRedisConnection != null) {
            return bashRedisConnection;
        }
        return bashClusterConnection;
    }

    public RedisStringReactiveCommands<byte[], byte[]> getBashStringReactiveCommands() {
        if (bashRedisReactiveCommands != null) {
            return bashRedisReactiveCommands;
        }
        return bashClusterReactiveCommands;
    }

    public StatefulRedisPubSubConnection<String, byte[]> getPubSubConnection() {
        if (pubSubConnection == null) {
            lock.lock();
            try {
                if (pubSubConnection == null) {
                    if (abstractRedisClient instanceof RedisClient) {
                        pubSubConnection = ((RedisClient) abstractRedisClient).connectPubSub(ComposedRedisCodec.getInstance(charset));
                    } else {
                        pubSubConnection = ((RedisClusterClient) abstractRedisClient).connectPubSub(ComposedRedisCodec.getInstance(charset));
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return pubSubConnection;
    }

    public RedisStringReactiveCommands<byte[], byte[]> getStringReactiveCommands() {
        if (redisReactiveCommands != null) {
            return redisReactiveCommands;
        }
        return clusterReactiveCommands;
    }

    public StatefulConnection<byte[], byte[]> getStatefulConnection() {
        if (redisConnection != null) {
            return redisConnection;
        }
        return clusterConnection;
    }

    public RedisKeyReactiveCommands<byte[], byte[]> getKeyReactiveCommands() {
        if (redisConnection != null) {
            return redisReactiveCommands;
        }
        return clusterReactiveCommands;
    }

    public RedisHashReactiveCommands<byte[], byte[]> getHashReactiveCommands() {
        if (redisConnection != null) {
            return redisReactiveCommands;
        }
        return clusterReactiveCommands;
    }

    @Override
    public void close() {
        reactiveClose().subscribe();
    }

    public Mono<Void> reactiveClose() {
        if (redisConnection != null) {
            return Mono.fromFuture(
                    redisConnection.closeAsync()
                            .thenCompose(v -> {
                                if (pubSubConnection != null) {
                                    return pubSubConnection.closeAsync();
                                }
                                return new CompletableFuture<>();
                            })
                            .thenCompose(v -> bashRedisConnection.closeAsync())
                            .thenCompose(v -> abstractRedisClient.shutdownAsync()));
        }
        return Mono.fromFuture(
                clusterConnection.closeAsync()
                        .thenCompose(v -> {
                            if (pubSubConnection != null) {
                                return pubSubConnection.closeAsync();
                            }
                            return new CompletableFuture<>();
                        })
                        .thenCompose(v -> bashClusterConnection.closeAsync())
                        .thenCompose(v -> abstractRedisClient.shutdownAsync()));
    }

}