package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.extension.redis.config.ClusterConfig;
import com.igeeksky.xcache.extension.redis.config.props.Cluster;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.resource.ClientResources;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceClusterConnectionProvider implements RedisConnectionFactory {

    private final ClusterConfig config;

    private final StatefulRedisClusterConnection<byte[], byte[]> connection;

    private final StatefulRedisClusterConnection<byte[], byte[]> bashConnection;

    public LettuceClusterConnectionProvider(ClusterConfig config) {
        this.config = config;
        this.connection = createConnection(config);
        this.bashConnection = createConnection(config);
    }

    private static StatefulRedisClusterConnection<byte[], byte[]> createConnection(ClusterConfig config) {
        ClientResources clientResources = config.getClientResources();
        Cluster cluster = config.getCluster();
        List<String> nodes = cluster.getNodes();
        List<RedisURI> redisURIS = new ArrayList<>();
        for (String node : nodes) {
            String[] hostAndPort = node.split(":");
            String host = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            redisURIS.add(createRedisURI(host, port, config));
        }
        RedisClusterClient redisClient = RedisClusterClient.create(clientResources, redisURIS);
        redisClient.setOptions(config.getClientOptions());
        StatefulRedisClusterConnection<byte[], byte[]> connect = redisClient.connect(ByteArrayCodec.INSTANCE);
        connect.setReadFrom(ReadFrom.valueOf(config.getReadFrom()));
        return connect;
    }

    private static RedisURI createRedisURI(String host, int port, ClusterConfig config) {
        // TODO 重复代码，使用抽象类
        RedisURI.Builder uriBuilder = RedisURI.Builder
                .redis(host, port)
                .withClientName(config.getClientName())
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
    public LettuceClusterConnection getConnection() {
        return new LettuceClusterConnection(this.connection, this.bashConnection);
    }

    @Override
    public LettuceClusterPubSubConnection getPubSubConnection() {
        StatefulRedisClusterPubSubConnection<String, byte[]> pubSubConnection = createPubSubConnection(config);
        return new LettuceClusterPubSubConnection(pubSubConnection);
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

    private static StatefulRedisClusterPubSubConnection<String, byte[]> createPubSubConnection(ClusterConfig config) {
        ClientResources clientResources = config.getClientResources();
        Cluster cluster = config.getCluster();
        List<String> nodes = cluster.getNodes();
        List<RedisURI> redisURIS = new ArrayList<>();
        for (String node : nodes) {
            String[] hostAndPort = node.split(":");
            String host = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            redisURIS.add(createRedisURI(host, port, config));
        }
        RedisClusterClient redisClient = RedisClusterClient.create(clientResources, redisURIS);
        redisClient.setOptions(config.getClientOptions());
        return redisClient.connectPubSub(ComposedRedisCodec.getInstance(config.getCharset()));
    }

}