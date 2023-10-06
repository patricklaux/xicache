package com.igeeksky.xcache.autoconfigure.redis.lettuce;

import com.igeeksky.xcache.autoconfigure.redis.RedisAutoConfiguration;
import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.support.lettuce.config.LettuceClusterConfig;
import com.igeeksky.xcache.support.lettuce.config.LettuceSentinelConfig;
import com.igeeksky.xcache.support.lettuce.config.LettuceStandaloneConfig;
import com.igeeksky.xcache.support.lettuce.config.props.ClientOptions;
import com.igeeksky.xcache.support.lettuce.config.props.ClusterClientOptions;
import com.igeeksky.xcache.support.lettuce.config.props.Lettuce;
import com.igeeksky.xcache.support.lettuce.LettuceClusterConnectionFactory;
import com.igeeksky.xcache.support.lettuce.LettuceConnectionFactory;
import com.igeeksky.xcache.support.lettuce.LettuceSentinelConnectionFactory;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.ClientResourcesBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RedisAutoConfiguration.class)
class LettuceAutoConfiguration {

    private final LettuceProperties lettuceProperties;

    public LettuceAutoConfiguration(LettuceProperties lettuceProperties) {
        this.lettuceProperties = lettuceProperties;
    }

    @Bean(destroyMethod = "shutdown")
    DefaultClientResources lettuceClientResources(ObjectProvider<ClientResourcesBuilderCustomizer> customizers) {
        DefaultClientResources.Builder builder = DefaultClientResources.builder();
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    @Bean
    RedisConnectionFactoryHolder redisConnectionFactoryHolder(
            DefaultClientResources res, ObjectProvider<ClientOptionsBuilderCustomizer> customizers) {

        Map<String, RedisConnectionFactory> connectionFactoryMap = new HashMap<>();

        List<Lettuce> connections = lettuceProperties.getConnections();
        for (Lettuce lettuce : connections) {
            if (lettuce.getSentinel() != null) {
                LettuceSentinelConfig config = lettuce.createSentinelConfig();
                io.lettuce.core.ClientOptions options = clientOptions(config.getClientOptions(), customizers);
                connectionFactoryMap.put(lettuce.getId(), new LettuceSentinelConnectionFactory(config, options, res));
            } else if (lettuce.getCluster() != null) {
                LettuceClusterConfig config = lettuce.createClusterConfig();
                io.lettuce.core.cluster.ClusterClientOptions options = clusterClientOptions(config.getClientOptions(), customizers);
                connectionFactoryMap.put(lettuce.getId(), new LettuceClusterConnectionFactory(config, options, res));
            } else if (lettuce.getStandalone() != null) {
                LettuceStandaloneConfig config = lettuce.createStandaloneConfig();
                io.lettuce.core.ClientOptions options = clientOptions(config.getClientOptions(), customizers);
                connectionFactoryMap.put(lettuce.getId(), new LettuceConnectionFactory(config, options, res));
            } else {
                // TODO 定义 Lettuce error
                throw new RuntimeException("lettuce:[" + lettuce.getId() + "] init error." + lettuce);
            }
        }

        return new RedisConnectionFactoryHolder(connectionFactoryMap);
    }

    private static io.lettuce.core.ClientOptions clientOptions(
            ClientOptions clientOptions, ObjectProvider<ClientOptionsBuilderCustomizer> customizers) {

        return null;
    }

    private static io.lettuce.core.cluster.ClusterClientOptions clusterClientOptions(
            ClusterClientOptions clientOptions, ObjectProvider<ClientOptionsBuilderCustomizer> customizers) {

        return null;
    }

}