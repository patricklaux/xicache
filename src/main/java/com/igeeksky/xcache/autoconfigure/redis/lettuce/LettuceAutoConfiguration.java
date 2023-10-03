package com.igeeksky.xcache.autoconfigure.redis.lettuce;

import com.igeeksky.xcache.autoconfigure.redis.RedisAutoConfiguration;
import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.extension.redis.config.RedisClusterConfig;
import com.igeeksky.xcache.extension.redis.config.RedisSentinelConfig;
import com.igeeksky.xcache.extension.redis.config.RedisStandaloneConfig;
import com.igeeksky.xcache.extension.redis.config.props.Lettuce;
import com.igeeksky.xcache.support.lettuce.LettuceClusterConnectionFactory;
import com.igeeksky.xcache.support.lettuce.LettuceConnectionFactory;
import com.igeeksky.xcache.support.lettuce.LettuceSentinelConnectionFactory;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
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
    RedisConnectionFactoryHolder lettuceConnectionFactoryHolder(DefaultClientResources res) {
        Map<String, RedisConnectionFactory> connectionFactoryMap = new HashMap<>();
        // 根据 lettuceProperties 生成 LettuceConnectionFactory
        List<Lettuce> connections = lettuceProperties.getConnections();
        for (Lettuce lettuce : connections) {
            RedisConnectionFactory connectionFactory = createConnectionFactory(lettuce, res);
            connectionFactoryMap.put(lettuce.getId(), connectionFactory);
        }
        return new RedisConnectionFactoryHolder(connectionFactoryMap);
    }

    private RedisConnectionFactory createConnectionFactory(Lettuce lettuce, DefaultClientResources res) {
        RedisType redisType = lettuce.getRedisType();
        if (RedisType.SENTINEL == redisType) {
            return createSentinelConnectionFactory(lettuce, res);
        }
        if (RedisType.CLUSTER == redisType) {
            return createClusterConnectionFactory(lettuce, res);
        }
        return createStandaloneConnectionFactory(lettuce, res);
    }

    private LettuceSentinelConnectionFactory createSentinelConnectionFactory(Lettuce lettuce, DefaultClientResources res) {
        RedisSentinelConfig config = lettuce.createSentinelConfig();
        ClientOptions clientOptions = getClientOptions(lettuce);
        return new LettuceSentinelConnectionFactory(config, clientOptions, res);
    }

    private LettuceClusterConnectionFactory createClusterConnectionFactory(Lettuce lettuce, DefaultClientResources res) {
        RedisClusterConfig config = lettuce.createClusterConfig();
        ClusterClientOptions clientOptions = getClusterClientOptions(lettuce);
        return new LettuceClusterConnectionFactory(config, clientOptions, res);
    }

    private LettuceConnectionFactory createStandaloneConnectionFactory(Lettuce lettuce, DefaultClientResources res) {
        RedisStandaloneConfig config = lettuce.createStandaloneConfig();
        ClientOptions clientOptions = getClientOptions(lettuce);
        return new LettuceConnectionFactory(config, clientOptions, res);
    }

    private ClientOptions getClientOptions(Lettuce lettuce) {
        return null;
    }

    private ClusterClientOptions getClusterClientOptions(Lettuce lettuce) {
        return null;
    }

}
