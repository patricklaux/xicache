package com.igeeksky.xcache.autoconfigure.redis;

import com.igeeksky.xcache.autoconfigure.Store;
import com.igeeksky.xcache.autoconfigure.XcacheManagerConfiguration;
import com.igeeksky.xcache.autoconfigure.redis.lettuce.RedisConnectionFactoryHolder;
import com.igeeksky.xcache.extension.redis.RedisCacheStoreProvider;
import com.igeeksky.xcache.extension.redis.RedisCacheSyncManager;
import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-02
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(XcacheManagerConfiguration.class)
public class RedisAutoConfiguration {

    private final RedisProperties redisProperties;

    public RedisAutoConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    RedisCacheStoreProviderHolder redisCacheStoreProviderHolder(RedisConnectionFactoryHolder factoryHolder) {
        Map<String, RedisCacheStoreProvider> map = new HashMap<>();
        List<Store> stores = redisProperties.getStores();
        for (Store store : stores) {
            String id = store.getId();
            String connection = store.getConnection();
            RedisConnectionFactory connectionFactory = factoryHolder.get(connection);
            // LettuceCacheStoreProvider storeProvider = new LettuceCacheStoreProvider(connectionFactory);
            // map.put(id, storeProvider);
        }
        return new RedisCacheStoreProviderHolder(map);
    }

    @Bean
    RedisCacheSyncProviderHolder redisCacheSyncProviderHolder(RedisConnectionFactoryHolder factoryHolder) {
        Map<String, RedisCacheSyncManager> map = new HashMap<>();
        List<Store> stores = redisProperties.getSyncs();
        for (Store store : stores) {
            String id = store.getId();
            String connection = store.getConnection();
            RedisConnectionFactory connectionFactory = factoryHolder.get(connection);
            // LettuceCacheStoreProvider storeProvider = new LettuceCacheStoreProvider(connectionFactory);
            // map.put(id, storeProvider);
        }
        return new RedisCacheSyncProviderHolder(map);
    }

}
