package com.igeeksky.xcache.autoconfigure.lettuce;

import com.igeeksky.xcache.autoconfigure.Store;
import com.igeeksky.xcache.autoconfigure.XcacheManagerConfiguration;
import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import com.igeeksky.xcache.extension.redis.config.ClusterConfig;
import com.igeeksky.xcache.extension.redis.config.StandaloneConfig;
import com.igeeksky.xcache.extension.redis.config.props.Lettuce;
import com.igeeksky.xcache.extension.redis.RedisCacheStoreProvider;
import com.igeeksky.xcache.support.lettuce.LettuceClusterConnectionProvider;
import com.igeeksky.xcache.support.lettuce.LettuceConnectionProvider;
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
@AutoConfigureBefore(XcacheManagerConfiguration.class)
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

    private LettuceConnectionProvider createSentinelConnectionFactory(Lettuce lettuce, DefaultClientResources res) {
        return null;
    }

    private LettuceClusterConnectionProvider createClusterConnectionFactory(Lettuce lettuce, DefaultClientResources res) {
        ClusterConfig config = lettuce.createClusterConfig();
        ClusterClientOptions clientOptions = getClusterClientOptions(lettuce);
        config.setClientOptions(clientOptions);
        return new LettuceClusterConnectionProvider(config);
    }

    private LettuceConnectionProvider createStandaloneConnectionFactory(Lettuce lettuce, DefaultClientResources res) {
        StandaloneConfig config = lettuce.createStandaloneConfig();
        config.setClientOptions(getClientOptions(lettuce));
        config.setClientResources(res);
        return new LettuceConnectionProvider(config);
    }

    private ClientOptions getClientOptions(Lettuce lettuce) {
        return null;
    }

    private ClusterClientOptions getClusterClientOptions(Lettuce lettuce) {
        // 判断是否集群
        return null;
    }

    @Bean
    LettuceCacheStoreProviderHolder lettuceCacheStoreProviderHolder(RedisConnectionFactoryHolder factoryHolder) {
        Map<String, RedisCacheStoreProvider> map = new HashMap<>();
        List<Store> stores = lettuceProperties.getStores();
        for (Store store : stores) {
            String id = store.getId();
            String connection = store.getConnection();
            RedisConnectionFactory connectionFactory = factoryHolder.get(connection);
            // LettuceCacheStoreProvider storeProvider = new LettuceCacheStoreProvider(connectionFactory);
            // map.put(id, storeProvider);
        }
        return new LettuceCacheStoreProviderHolder(map);
    }

}
