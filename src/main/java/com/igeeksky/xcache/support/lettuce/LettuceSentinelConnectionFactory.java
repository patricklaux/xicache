package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.config.HostAndPort;
import com.igeeksky.xcache.support.lettuce.config.LettuceSentinelConfig;
import com.igeeksky.xtool.core.io.IOUtils;
import com.igeeksky.xtool.core.lang.StringUtils;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.resource.ClientResources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceSentinelConnectionFactory implements RedisConnectionFactory {

    private final Lock lock = new ReentrantLock();
    private final RedisURI redisURI;
    private final RedisClient redisClient;
    private final LettuceSentinelConfig config;
    private final LettuceConnection lettuceConnection;
    private volatile LettucePubSubConnection pubSubConnection;

    public LettuceSentinelConnectionFactory(LettuceSentinelConfig config, ClientOptions options, ClientResources res) {
        this.config = config;
        this.redisURI = redisUri(config);
        this.redisClient = redisClient(options, res);
        StatefulRedisMasterReplicaConnection<byte[], byte[]> connection = connection(config, redisClient, redisURI);
        StatefulRedisMasterReplicaConnection<byte[], byte[]> bashConnection = connection(config, redisClient, redisURI);
        this.lettuceConnection = new LettuceConnection(connection, bashConnection);
    }

    private static StatefulRedisMasterReplicaConnection<byte[], byte[]> connection(
            LettuceSentinelConfig config, RedisClient client, RedisURI redisURI) {

        ByteArrayCodec codec = ByteArrayCodec.INSTANCE;
        StatefulRedisMasterReplicaConnection<byte[], byte[]> connection = MasterReplica.connect(client, codec, redisURI);
        connection.setReadFrom(config.getReadFrom());
        return connection;
    }

    private static RedisClient redisClient(ClientOptions options, ClientResources res) {
        RedisClient redisClient = RedisClient.create(res);
        redisClient.setOptions(options);
        return redisClient;
    }

    private static RedisURI redisUri(LettuceSentinelConfig config) {
        RedisURI.Builder builder = LettuceHelper.redisURIBuilder(config, config.getMasterId());
        List<RedisURI> sentinels = sentinels(config);
        for (RedisURI sentinel : sentinels) {
            builder.withSentinel(sentinel);
        }
        return builder.build();
    }

    private static List<RedisURI> sentinels(LettuceSentinelConfig config) {
        List<RedisURI> sentinels = new ArrayList<>();
        List<HostAndPort> nodes = config.getSentinels();
        for (HostAndPort node : nodes) {
            String host = node.getHost();
            int port = node.getPort();
            String sentinelUsername = config.getSentinelUsername();
            String sentinelPassword = config.getSentinelPassword();
            RedisURI sentinelURI = RedisURI.create(host, port);
            if (StringUtils.hasText(sentinelUsername)) {
                sentinelURI.setUsername(sentinelUsername);
            }
            if (StringUtils.hasText(sentinelPassword)) {
                sentinelURI.setPassword(sentinelPassword.toCharArray());
            }
            sentinels.add(sentinelURI);
        }
        return sentinels;
    }

    @Override
    public LettuceConnection getConnection() {
        return this.lettuceConnection;
    }

    @Override
    public LettucePubSubConnection getPubSubConnection() {
        if (this.pubSubConnection == null) {
            ComposedRedisCodec codec = ComposedRedisCodec.getInstance(config.getCharset());
            lock.lock();
            try {
                if (this.pubSubConnection == null) {
                    this.pubSubConnection = new LettucePubSubConnection(redisClient.connectPubSub(codec, redisURI));
                }
            } finally {
                lock.unlock();
            }
        }
        return this.pubSubConnection;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(this.lettuceConnection, this.pubSubConnection);
    }

}