package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.redis.RedisHashCacheStore;
import com.igeeksky.xcache.extension.redis.RedisStringCacheStore;
import com.igeeksky.xcache.extension.serializer.Serializer;
import com.igeeksky.xcache.extension.serializer.StringSerializer;
import com.igeeksky.xcache.store.CacheStore;
import com.igeeksky.xcache.store.CacheStoreProvider;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-07-27
 */
public class LettuceCacheStoreProvider implements CacheStoreProvider {

    private final LettuceRedisHashWriter redisHashWriter;

    private final LettuceRedisStringWriter redisStringWriter;

    private final LettuceConnectionManager connectionManager;

    private final StringSerializer.StringSerializerProvider stringSerializerProvider = StringSerializer.StringSerializerProvider.getInstance();

    // 配置文件
    public LettuceCacheStoreProvider(LettuceConnectionManager connectionManager) {
        this.connectionManager = connectionManager;

        // TODO 完善此类
        // 前置操作：
        // 1. LettuceConfiguration 获取配置文件
        // 2. LettuceConfiguration 生成 LettuceConnectionManager
        // 3. LettuceConfiguration 生成 LettuceCacheStoreProvider(LettuceConnectionManager)
        // 4. LettuceConfiguration 生成 LettuceCacheSyncProvider (LettuceConnectionManager)
        // 此类操作
        // 1. 生成唯一的 RedisStringWriter， RedisHashWriter
        this.redisHashWriter = new LettuceRedisHashWriter(connectionManager);
        this.redisStringWriter = new LettuceRedisStringWriter(connectionManager);
    }

    @Override
    @SuppressWarnings("unchecked")
    // TODO 完善泛型
    public <K, V> CacheStore<K, V> get(CacheConfig<K, V> config) {
        Charset charset = config.getCharset();
        StringSerializer serializer = StringSerializer.getInstance(charset);
        String storeName = StringUtils.toLowerCase(config.getStoreName());
        if (storeName == null || Objects.equals(storeName, RedisStringCacheStore.STORE_NAME)) {
            return (CacheStore<K, V>) new RedisStringCacheStore<>(config, serializer, this.redisStringWriter);
        }
        return (CacheStore<K, V>) new RedisHashCacheStore<>(config, serializer, this.redisHashWriter);
    }
}
