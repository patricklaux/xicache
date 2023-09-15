package com.igeeksky.xcache.support.lettuce;

import io.lettuce.core.AbstractRedisClient;

import java.nio.charset.Charset;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-14
 */
public class LettuceCacheSyncConfig {

    /**
     * sync-channel:*
     */
    private String pattern;

    private Charset charset;

    private AbstractRedisClient redisClient;

    private LettuceCacheMessageListener listener;

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

    public LettuceCacheMessageListener getListener() {
        return listener;
    }

    public void setListener(LettuceCacheMessageListener listener) {
        this.listener = listener;
    }
}
