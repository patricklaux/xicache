package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.ExpiryCacheValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.serializer.JdkSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-14
 */
class RandomRangeCacheExpiryTest {

    Cache<Object, CacheValue<Object>> cache;

    @BeforeEach
    void setUp() {
        RandomRangeCacheExpiry<Object, Object> expiry = new RandomRangeCacheExpiry<>(Duration.ofSeconds(2), Duration.ofSeconds(2));
        cache = Caffeine.newBuilder()
                .expireAfter(expiry)
                // .expireAfterWrite(2, TimeUnit.SECONDS)
                // .expireAfterAccess(2, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
    }

    @Test
    void get() throws InterruptedException {
        cache.put("a", new CacheValue<>("a"));
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            cache.getIfPresent("a");
        }
        cache.put("a", new CacheValue<>("a"));
    }

}