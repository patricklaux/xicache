package com.igeeksky.xcache.extension.redis;

import io.lettuce.core.AbstractRedisClient;

import java.nio.charset.Charset;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-14
 */
public class RedisCacheSyncConfig {

    /**
     * sync-channel:*
     */
    private String pattern;

    private Charset charset;

    private AbstractRedisClient redisClient;

    private RedisCacheMessageListener listener;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public AbstractRedisClient getRedisClient() {
        return redisClient;
    }

    public void setRedisClient(AbstractRedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public RedisCacheMessageListener getListener() {
        return listener;
    }

    public void setListener(RedisCacheMessageListener listener) {
        this.listener = listener;
    }
}
