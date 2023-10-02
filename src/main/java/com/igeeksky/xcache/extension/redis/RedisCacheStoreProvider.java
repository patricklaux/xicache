package com.igeeksky.xcache.extension.redis;

import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.redis.*;
import com.igeeksky.xcache.extension.serializer.StringSerializer;
import com.igeeksky.xcache.store.RemoteCacheStore;
import com.igeeksky.xcache.store.RemoteCacheStoreProvider;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-07-27
 */
public class RedisCacheStoreProvider implements RemoteCacheStoreProvider {

    private final RedisConnection redisConnection;

    private final RedisConnectionFactory connectionFactory;

    public RedisCacheStoreProvider(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.redisConnection = connectionFactory.getConnection();
    }

    @Override
    public <K, V> RemoteCacheStore getRemoteCacheStore(CacheConfig<K, V> config) {
        Charset charset = config.getCharset();
        StringSerializer serializer = StringSerializer.getInstance(charset);
        String storeName = StringUtils.toLowerCase(config.getRemoteConfig().getStoreName());
        if (storeName == null || Objects.equals(storeName, RedisStringCacheStore.STORE_NAME)) {
            return new RedisStringCacheStore(config, serializer, this.redisConnection);
        }
        return new RedisHashCacheStore<>(config, serializer, this.redisConnection);
    }

    @Override
    public void close() throws Exception {
        this.connectionFactory.close();
    }
}
