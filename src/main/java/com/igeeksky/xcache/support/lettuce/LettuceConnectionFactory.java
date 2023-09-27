package com.igeeksky.xcache.support.lettuce;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisClusterReactiveCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-17
 */
public class LettuceConnectionFactory implements AutoCloseable {

    private final ReentrantLock lock = new ReentrantLock();

    private final boolean cluster;
    private final Charset charset;

    private final RedisReactiveCommands<byte[], byte[]> redisReactiveCommands;
    private final RedisReactiveCommands<byte[], byte[]> bashRedisReactiveCommands;
    private final RedisClusterReactiveCommands<byte[], byte[]> clusterReactiveCommands;
    private final RedisClusterReactiveCommands<byte[], byte[]> bashClusterReactiveCommands;

    private final AbstractRedisClient abstractRedisClient;
    private final StatefulRedisConnection<byte[], byte[]> redisConnection;
    private final StatefulRedisConnection<byte[], byte[]> bashRedisConnection;
    private final StatefulRedisClusterConnection<byte[], byte[]> clusterConnection;
    private final StatefulRedisClusterConnection<byte[], byte[]> bashClusterConnection;
    private volatile StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;

    public LettuceConnectionFactory(AbstractRedisClient abstractRedisClient, Charset charset) {
        // TODO 传入配置文件，然后生成连接（单点，主从，哨兵, 集群配置）
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

    public RedisReactiveCommands<byte[], byte[]> getRedisReactiveCommands() {
        return redisReactiveCommands;
    }

    public RedisReactiveCommands<byte[], byte[]> getBashRedisReactiveCommands() {
        return bashRedisReactiveCommands;
    }

    public RedisClusterReactiveCommands<byte[], byte[]> getClusterReactiveCommands() {
        return clusterReactiveCommands;
    }

    public RedisClusterReactiveCommands<byte[], byte[]> getBashClusterReactiveCommands() {
        return bashClusterReactiveCommands;
    }

    public StatefulRedisPubSubConnection<String, byte[]> getPubSubConnection() {
        if (pubSubConnection == null) {
            ComposedRedisCodec redisCodec = ComposedRedisCodec.getInstance(charset);
            lock.lock();
            try {
                if (pubSubConnection == null) {
                    if (abstractRedisClient instanceof RedisClient) {
                        pubSubConnection = ((RedisClient) abstractRedisClient).connectPubSub(redisCodec);
                    } else {
                        pubSubConnection = ((RedisClusterClient) abstractRedisClient).connectPubSub(redisCodec);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return pubSubConnection;
    }

    @Override
    public void close() {
        close(redisConnection);
        close(bashRedisConnection);
        close(pubSubConnection);
        close(clusterConnection);
        close(bashClusterConnection);
        abstractRedisClient.shutdown();
    }

    private static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

}