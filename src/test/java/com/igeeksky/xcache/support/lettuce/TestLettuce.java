package com.igeeksky.xcache.support.lettuce;

import io.lettuce.core.*;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.protocol.ProtocolVersion;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class TestLettuce {

    // ClientResources ---start
    private int ioThreadPoolSize = 4;
    private int computationThreadPoolSize = 4;
    // ClientResources ---end

    // Standalone ---start
    private String clientName = null;
    private String username = "";
    private String password = "";
    private String host = "localhost";
    private int port = 6379;
    private int database = 0;

    // Standalone ---end

    // Sentinel ---start
    private String master;
    private String nodes;
    private long timeout;
    private boolean ssl;
    private boolean startTls;
    private boolean verifyPeer;
    private SslVerifyMode sslVerifyMode;

    // Sentinel ---end

    // ClientOptions ---start

    // ClientOptions ---end

    @Test
    void testStandalone() {
        // 创建 ClientResources
        ClientResources res = clientResources();
        // 创建 RedisURI
        RedisURI.Builder uriBuilder = RedisURI.builder();
        uriBuilder = uriBuilder
                .withClientName(clientName)
                .withHost(host)
                .withPort(port)
                .withDatabase(database)
                .withAuthentication(username, password)
                .withSentinelMasterId(master)
                .withTimeout(Duration.ofMillis(timeout))
                .withSsl(ssl)
                .withStartTls(startTls)
                .withVerifyPeer(verifyPeer)
                .withVerifyPeer(sslVerifyMode);

        RedisURI redisURI = uriBuilder.build();
        ClientOptions clientOptions = ClientOptions.builder()
                .pingBeforeActivateConnection(false)
                .autoReconnect(false)
                .cancelCommandsOnReconnectFailure(false)
                //.decodeBufferPolicy()
                .suspendReconnectOnProtocolFailure(false)
                .requestQueueSize(Integer.MAX_VALUE)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
                .protocolVersion(ProtocolVersion.newestSupported())
                .scriptCharset(StandardCharsets.UTF_8)
                .socketOptions(SocketOptions.create())
                .sslOptions(SslOptions.builder().build())
                .timeoutOptions(TimeoutOptions.create())
                .publishOnScheduler(false)
                .build();
        RedisClient redisClient = RedisClient.create(res, redisURI);
        redisClient.setOptions(clientOptions);
    }

    @Test
    void testSentinel() {
        // 创建 ClientResources
        ClientResources res = clientResources();

        // 创建 RedisURI
        RedisURI.Builder uriBuilder = RedisURI.Builder
                .sentinel(host, port, master)
                .withDatabase(database)
                .withAuthentication(username, password)
                .withClientName(clientName)
                .withSsl(ssl)
                .withStartTls(startTls)
                .withTimeout(Duration.ofMillis(timeout))
                .withVerifyPeer(verifyPeer)
                .withVerifyPeer(sslVerifyMode);
        String[] array = nodes.split(",");
        for (String node : array) {
            String[] split = node.split(":");
            uriBuilder.withSentinel(split[0], Integer.parseInt(split[1]));
        }

        RedisURI redisURI = uriBuilder.build();
        ClientOptions clientOptions = ClientOptions.builder()
                .pingBeforeActivateConnection(false)
                .autoReconnect(false)
                .cancelCommandsOnReconnectFailure(false)
                //.decodeBufferPolicy()
                .suspendReconnectOnProtocolFailure(false)
                .requestQueueSize(Integer.MAX_VALUE)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
                .protocolVersion(ProtocolVersion.newestSupported())
                .scriptCharset(StandardCharsets.UTF_8)
                .socketOptions(SocketOptions.create())
                .sslOptions(SslOptions.builder().build())
                .timeoutOptions(TimeoutOptions.create())
                .publishOnScheduler(false)
                .build();
        RedisClient redisClient = RedisClient.create(res, redisURI);
        redisClient.setOptions(clientOptions);
    }

    void testCluster() {
        // 创建 ClientResources （Netty IO及线程设置）
        ClientResources res = clientResources();

        // 创建 ClusterTopologyRefreshOptions （集群拓扑信息刷新）
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(false)
                //.refreshPeriod(Duration.ofMinutes(10))
                //.enablePeriodicRefresh(Duration.ofMinutes(10))
                .enableAllAdaptiveRefreshTriggers()
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30))
                .refreshTriggersReconnectAttempts(5)
                .dynamicRefreshSources(true)
                .closeStaleConnections(true)
                .build();

        // 创建集群 URI


        RedisClusterClient redisClusterClient = RedisClusterClient.create(res, new ArrayList<>());
        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyRefreshOptions) // cluster
                .maxRedirects(5)                                // cluster
                .nodeFilter(null)                               // cluster
                .validateClusterNodeMembership(true)            // cluster
                .pingBeforeActivateConnection(false)
                .autoReconnect(false)
                .cancelCommandsOnReconnectFailure(false)
                //.decodeBufferPolicy()
                .suspendReconnectOnProtocolFailure(false)
                .requestQueueSize(Integer.MAX_VALUE)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
                .protocolVersion(ProtocolVersion.newestSupported())
                .scriptCharset(StandardCharsets.UTF_8)
                .socketOptions(SocketOptions.create())
                .sslOptions(SslOptions.builder().build())
                .timeoutOptions(TimeoutOptions.create())
                .publishOnScheduler(false)
                .build());
    }

    @ConditionalOnMissingBean(ClientResources.class)
    private ClientResources clientResources() {
        DefaultClientResources.Builder resBuilder = DefaultClientResources.builder();
        return resBuilder.ioThreadPoolSize(ioThreadPoolSize)
                .computationThreadPoolSize(computationThreadPoolSize)
                .addressResolverGroup(DefaultAddressResolverGroup.INSTANCE)
                .build();
    }

}