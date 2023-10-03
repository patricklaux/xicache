package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.extension.redis.config.RedisClusterConfig;
import com.igeeksky.xcache.extension.redis.config.RedisNode;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.resource.ClientResources;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceClusterConnectionFactory implements RedisConnectionFactory {

    private final Lock lock = new ReentrantLock();

    private final RedisClusterConfig config;

    private final RedisClusterClient redisClient;

    private final LettuceClusterConnection lettuceConnection;

    private volatile LettuceClusterPubSubConnection pubSubConnection;

    public LettuceClusterConnectionFactory(RedisClusterConfig config, ClusterClientOptions options, ClientResources res) {
        this.config = config;
        this.redisClient = redisClient(config, options, res);
        StatefulRedisClusterConnection<byte[], byte[]> connection = connection(redisClient, config, false);
        StatefulRedisClusterConnection<byte[], byte[]> bashConnection = connection(redisClient, config, true);
        this.lettuceConnection = new LettuceClusterConnection(connection, bashConnection);
    }

    private static StatefulRedisClusterConnection<byte[], byte[]> connection(
            RedisClusterClient redisClient, RedisClusterConfig config, boolean autoFlush) {

        StatefulRedisClusterConnection<byte[], byte[]> connection = redisClient.connect(ByteArrayCodec.INSTANCE);
        connection.setReadFrom(ReadFrom.valueOf(config.getReadFrom()));
        connection.setAutoFlushCommands(autoFlush);
        return connection;
    }

    private static RedisClusterClient redisClient(RedisClusterConfig config, ClusterClientOptions options, ClientResources resources) {
        List<RedisURI> redisURIS = new ArrayList<>();
        List<RedisNode> nodes = config.getNodes();
        for (RedisNode node : nodes) {
            redisURIS.add(LettuceHelper.redisURI(config.getGeneric(), node.getHost(), node.getPort()));
        }
        RedisClusterClient redisClient = RedisClusterClient.create(resources, redisURIS);
        redisClient.setOptions(options);
        return redisClient;
    }

    @Override
    public LettuceClusterConnection getConnection() {
        return this.lettuceConnection;
    }

    @Override
    public LettuceClusterPubSubConnection getPubSubConnection() {
        if (this.pubSubConnection == null) {
            lock.lock();
            try {
                if (this.pubSubConnection == null) {
                    ComposedRedisCodec redisCodec = ComposedRedisCodec.getInstance(config.getCharset());
                    this.pubSubConnection = new LettuceClusterPubSubConnection(redisClient.connectPubSub(redisCodec));
                }
            } finally {
                lock.unlock();
            }
        }
        return this.pubSubConnection;
    }

    @Override
    public void close() throws Exception {
        closeQuietly(this.lettuceConnection, this.pubSubConnection);
    }

    private static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

}