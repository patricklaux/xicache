package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.autoconfigure.redis.lettuce.RedisType;
import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.extension.redis.config.RedisGenericConfig;
import com.igeeksky.xcache.extension.redis.config.RedisNode;
import com.igeeksky.xcache.extension.redis.config.RedisStandaloneConfig;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
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
public class LettuceConnectionFactory implements RedisConnectionFactory {

    private final ClientOptions options;
    private final ClientResources resources;
    private final RedisStandaloneConfig config;

    private final StatefulRedisConnection<byte[], byte[]> connection;

    private final StatefulRedisConnection<byte[], byte[]> bashConnection;

    public LettuceConnectionFactory(RedisStandaloneConfig config, ClientOptions options, ClientResources resources) {
        this.config = config;
        this.options = options;
        this.resources = resources;
        this.connection = createConnection(config);
        this.bashConnection = createConnection(config);
        this.bashConnection.setAutoFlushCommands(false);
    }

    private StatefulRedisConnection<byte[], byte[]> createConnection(RedisStandaloneConfig config) {
        RedisType redisType = config.getRedisType();
        RedisGenericConfig generic = config.getGeneric();
        RedisClient redisClient = RedisClient.create(resources);
        redisClient.setOptions(options);
        RedisURI redisURI = LettuceHelper.redisURI(generic, config.getHost(), config.getPort());
        // 创建 Standalone[单点连接]
        if (RedisType.SINGLE == redisType) {
            return redisClient.connect(ByteArrayCodec.INSTANCE, redisURI);
        }

        // 创建 Standalone[主从连接]，未配置副本节点，通过主节点发现副本
        List<RedisNode> replicas = config.getReplicas();
        if (CollectionUtils.isEmpty(replicas)) {
            StatefulRedisMasterReplicaConnection<byte[], byte[]> connect = MasterReplica.connect(redisClient, ByteArrayCodec.INSTANCE, redisURI);
            connect.setReadFrom(ReadFrom.valueOf(config.getReadFrom()));
            return connect;
        }

        List<RedisURI> redisURIS = new ArrayList<>();
        redisURIS.add(redisURI);
        for (RedisNode replica : replicas) {
            redisURIS.add(LettuceHelper.redisURI(generic, replica.getHost(), replica.getPort()));
        }

        StatefulRedisMasterReplicaConnection<byte[], byte[]> connect = MasterReplica.connect(redisClient, ByteArrayCodec.INSTANCE, redisURIS);
        connect.setReadFrom(ReadFrom.valueOf(config.getReadFrom()));
        return connect;
    }

    @Override
    public LettuceConnection getConnection() {
        return new LettuceConnection(this.connection, this.bashConnection);
    }

    @Override
    public LettucePubSubConnection getPubSubConnection() {
        StatefulRedisPubSubConnection<String, byte[]> pubSubConnection = createPubSubConnection(config);
        return new LettucePubSubConnection(pubSubConnection);
    }

    private StatefulRedisPubSubConnection<String, byte[]> createPubSubConnection(RedisStandaloneConfig config) {
        RedisClient redisClient = RedisClient.create(resources);
        redisClient.setOptions(options);
        RedisURI redisURI = LettuceHelper.redisURI(config.getGeneric(), config.getHost(), config.getPort());
        return redisClient.connectPubSub(ComposedRedisCodec.getInstance(config.getCharset()), redisURI);
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
        this.bashConnection.close();
    }
}
