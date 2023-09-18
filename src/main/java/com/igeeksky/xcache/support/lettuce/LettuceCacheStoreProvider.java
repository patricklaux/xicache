package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.redis.RedisHashCacheStore;
import com.igeeksky.xcache.extension.redis.RedisStringCacheStore;
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
public class LettuceCacheStoreProvider implements RemoteCacheStoreProvider {

    private final LettuceRedisHashWriter redisHashWriter;

    private final LettuceRedisStringWriter redisStringWriter;

    public LettuceCacheStoreProvider(LettuceConnectionManager connectionManager) {
        this.redisHashWriter = new LettuceRedisHashWriter(connectionManager);
        this.redisStringWriter = new LettuceRedisStringWriter(connectionManager);
    }

    @Override
    public <K, V> RemoteCacheStore getRemoteCacheStore(CacheConfig<K, V> config) {
        Charset charset = config.getCharset();
        StringSerializer serializer = StringSerializer.getInstance(charset);
        String storeName = StringUtils.toLowerCase(config.getStoreName());
        if (storeName == null || Objects.equals(storeName, RedisStringCacheStore.STORE_NAME)) {
            return new RedisStringCacheStore(config, serializer, this.redisStringWriter);
        }
        return new RedisHashCacheStore<>(config, serializer, this.redisHashWriter);
    }

}
