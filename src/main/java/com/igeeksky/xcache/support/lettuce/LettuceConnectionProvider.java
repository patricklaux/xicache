package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.autoconfigure.lettuce.RedisType;
import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.extension.redis.config.StandaloneConfig;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceConnectionProvider implements RedisConnectionFactory {

    private final StandaloneConfig config;

    private final StatefulRedisConnection<byte[], byte[]> connection;

    private final StatefulRedisConnection<byte[], byte[]> bashConnection;

    public LettuceConnectionProvider(StandaloneConfig config) {
        this.config = config;
        this.connection = createConnection(config);
        this.bashConnection = createConnection(config);
    }

    private static StatefulRedisConnection<byte[], byte[]> createConnection(StandaloneConfig config) {
        RedisType redisType = config.getRedisType();
        ClientResources clientResources = config.getClientResources();
        RedisClient redisClient = RedisClient.create(clientResources);
        redisClient.setOptions(config.getClientOptions());
        RedisURI redisURI = createRedisURI(config.getHost(), config.getPort(), config);
        // 创建 Standalone[单点连接]
        if (RedisType.SINGLE == redisType) {
            return redisClient.connect(ByteArrayCodec.INSTANCE, redisURI);
        }

        // 创建 Standalone[主从连接]，未配置副本节点，通过主节点发现副本
        List<String> replicas = config.getReplicas();
        if (CollectionUtils.isEmpty(replicas)) {
            StatefulRedisMasterReplicaConnection<byte[], byte[]> connect = MasterReplica.connect(redisClient, ByteArrayCodec.INSTANCE, redisURI);
            connect.setReadFrom(ReadFrom.valueOf(config.getReadFrom()));
            return connect;
        }

        List<RedisURI> redisURIS = new ArrayList<>();
        redisURIS.add(redisURI);
        for (String replica : replicas) {
            String[] hostAndPort = replica.split(":");
            String host = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            redisURIS.add(createRedisURI(host, port, config));
        }
        StatefulRedisMasterReplicaConnection<byte[], byte[]> connect = MasterReplica.connect(redisClient, ByteArrayCodec.INSTANCE, redisURIS);
        connect.setReadFrom(ReadFrom.valueOf(config.getReadFrom()));
        return connect;
    }

    private static RedisURI createRedisURI(String host, int port, StandaloneConfig config) {
        RedisURI.Builder uriBuilder = RedisURI.Builder
                .redis(host, port)
                .withClientName(config.getClientName())
                .withDatabase(config.getDatabase())
                .withSsl(config.isSsl())
                .withStartTls(config.isStartTls())
                .withTimeout(Duration.ofMillis(config.getTimeout()))
                .withVerifyPeer(config.isVerifyPeer())
                .withVerifyPeer(config.getSslVerifyMode());
        String username = config.getUsername();
        String password = config.getPassword();
        if (username != null && password != null) {
            uriBuilder.withAuthentication(username, password);
        } else if (username == null && password != null) {
            uriBuilder.withPassword(password.toCharArray());
        }
        return uriBuilder.build();
    }

    @Override
    public LettuceConnection getConnection() {
        return new LettuceConnection(this.connection, this.connection);
    }

    @Override
    public LettucePubSubConnection getPubSubConnection() {
        StatefulRedisPubSubConnection<String, byte[]> pubSubConnection = createPubSubConnection(config);
        return new LettucePubSubConnection(pubSubConnection);
    }

    private static StatefulRedisPubSubConnection<String, byte[]> createPubSubConnection(StandaloneConfig config) {
        ClientResources clientResources = config.getClientResources();
        RedisClient redisClient = RedisClient.create(clientResources);
        redisClient.setOptions(config.getClientOptions());
        RedisURI redisURI = createRedisURI(config.getHost(), config.getPort(), config);
        return redisClient.connectPubSub(ComposedRedisCodec.getInstance(config.getCharset()), redisURI);
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
