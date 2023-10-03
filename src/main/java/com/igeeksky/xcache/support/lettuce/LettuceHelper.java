package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.redis.config.RedisGenericConfig;
import io.lettuce.core.RedisURI;

import java.time.Duration;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-03
 */
public abstract class LettuceHelper {

    public static RedisURI redisURI(RedisGenericConfig config, String host, int port) {
        return redisURIBuilder(config).withHost(host).withPort(port).build();
    }

    public static RedisURI.Builder redisURIBuilder(RedisGenericConfig config, String masterId) {
        return redisURIBuilder(config).withSentinelMasterId(masterId);
    }

    public static RedisURI.Builder redisURIBuilder(RedisGenericConfig config) {
        RedisURI.Builder uriBuilder = RedisURI.builder()
                .withClientName(config.getClientName())
                .withDatabase(config.getDatabase())
                .withSsl(config.isSsl())
                .withStartTls(config.isStartTls())
                .withTimeout(Duration.ofMillis(config.getTimeout()))
                .withVerifyPeer(config.isVerifyPeer())
                .withVerifyPeer(config.getSslVerifyMode());
        String username = config.getUsername();
        String password = config.getPassword();
        if (username != null && password != null) {
            uriBuilder.withAuthentication(username, password);
        } else if (username == null && password != null) {
            uriBuilder.withPassword(password.toCharArray());
        }
        return uriBuilder;
    }

}
