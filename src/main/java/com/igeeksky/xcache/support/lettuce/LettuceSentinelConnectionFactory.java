package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.extension.redis.config.RedisNode;
import com.igeeksky.xcache.extension.redis.config.RedisSentinelConfig;
import com.igeeksky.xtool.core.lang.StringUtils;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceSentinelConnectionFactory implements RedisConnectionFactory {

    private final RedisURI redisURI;
    private final RedisClient redisClient;
    private final RedisSentinelConfig config;
    private final StatefulRedisMasterReplicaConnection<byte[], byte[]> redisConnection;
    private final StatefulRedisMasterReplicaConnection<byte[], byte[]> bashConnection;

    public LettuceSentinelConnectionFactory(RedisSentinelConfig config, ClientOptions options, ClientResources res) {
        this.config = config;
        this.redisURI = redisUri(config);
        this.redisClient = redisClient(options, res);
        this.redisConnection = createConnection(config, redisClient, redisURI);
        this.bashConnection = createConnection(config, redisClient, redisURI);
        this.bashConnection.setAutoFlushCommands(false);
    }

    private static StatefulRedisMasterReplicaConnection<byte[], byte[]> createConnection(
            RedisSentinelConfig config, RedisClient client, RedisURI redisURI) {

        ByteArrayCodec codec = ByteArrayCodec.INSTANCE;
        StatefulRedisMasterReplicaConnection<byte[], byte[]> connection = MasterReplica.connect(client, codec, redisURI);
        connection.setReadFrom(ReadFrom.valueOf(config.getReadFrom()));
        return connection;
    }

    private static RedisClient redisClient(ClientOptions options, ClientResources res) {
        RedisClient redisClient = RedisClient.create(res);
        redisClient.setOptions(options);
        return redisClient;
    }

    private static RedisURI redisUri(RedisSentinelConfig config) {
        RedisURI.Builder builder = LettuceHelper.redisURIBuilder(config.getGeneric(), config.getMasterId());
        List<RedisURI> sentinels = sentinels(config);
        for (RedisURI sentinel : sentinels) {
            builder.withSentinel(sentinel);
        }
        return builder.build();
    }

    private static List<RedisURI> sentinels(RedisSentinelConfig config) {
        List<RedisURI> sentinels = new ArrayList<>();
        List<RedisNode> nodes = config.getSentinels();
        for (RedisNode node : nodes) {
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
        return new LettuceConnection(this.redisConnection, this.bashConnection);
    }

    @Override
    public LettucePubSubConnection getPubSubConnection() {
        ComposedRedisCodec redisCodec = ComposedRedisCodec.getInstance(config.getCharset());
        StatefulRedisPubSubConnection<String, byte[]> pubSubConnection = redisClient.connectPubSub(redisCodec, redisURI);
        return new LettucePubSubConnection(pubSubConnection);
    }

    @Override
    public void close() throws Exception {
        this.redisConnection.close();
        this.bashConnection.close();
    }

}