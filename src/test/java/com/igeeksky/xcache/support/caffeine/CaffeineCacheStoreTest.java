package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.serializer.JdkSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-12
 */
class CaffeineCacheStoreTest {

    private CaffeineCacheStore<String, String> cache;

    @BeforeEach
    void setUp() {
        CacheConfig<String, String> config = new CacheConfig<>();
        config.setValueSerializer(new JdkSerializer<>());
        config.setName("user");
        Cache<Object, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
        cache = new CaffeineCacheStore<>(config, caffeine);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void get() throws InterruptedException {
        cache.put("a", Mono.just("a")).subscribe();
        Thread.sleep(2880);
        Mono<CacheValue<String>> mono = cache.get("a");
        mono.filter(Objects::nonNull).doOnNext(cv -> System.out.println(cv.getValue())).subscribe();
    }

    @Test
    void getBytes() throws InterruptedException {
        CacheConfig<byte[], byte[]> config = new CacheConfig<>();
        config.setValueSerializer(new JdkSerializer<>());
        config.setName("user");
        Cache<Object, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
        CaffeineCacheStore<byte[], byte[]> cache = new CaffeineCacheStore<>(config, caffeine);

        String str = "hash";
        byte[] source = str.getBytes(StandardCharsets.UTF_8);
        cache.put(source, Mono.just(source)).subscribe();

        cache.get(source).subscribe(cacheValue -> {
            Assertions.assertNotNull(cacheValue);
            Assertions.assertNotNull(cacheValue.getValue());
            System.out.println(new String(cacheValue.getValue()));
        });
    }

    @Test
    void getBytes2() throws InterruptedException {
        CacheConfig<byte[], byte[]> config = new CacheConfig<>();
        config.setValueSerializer(new JdkSerializer<>());
        config.setName("user");
        Cache<Object, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
        CaffeineCacheStore<byte[], byte[]> cache = new CaffeineCacheStore<>(config, caffeine);

        String str = "hash";
        byte[] source = str.getBytes(StandardCharsets.UTF_8);
        cache.put(source, Mono.just(source)).subscribe();

        byte[] source2 = str.getBytes(StandardCharsets.UTF_8);
        cache.get(source2).subscribe(Assertions::assertNull);
    }

    @Test
    void doGetAll() {
        CacheConfig<byte[], byte[]> config = new CacheConfig<>();
        config.setValueSerializer(new JdkSerializer<>());
        config.setName("user");
        Cache<Object, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
        CaffeineCacheStore<byte[], byte[]> cache = new CaffeineCacheStore<>(config, caffeine);

        String str = "hash";
        byte[] source = str.getBytes(StandardCharsets.UTF_8);
        cache.put(source, Mono.just(source)).subscribe();

        byte[] source2 = str.getBytes(StandardCharsets.UTF_8);

        Set<byte[]> set = new HashSet<>();
        set.add(source);
        set.add(source2);
        cache.doGetAll(set).subscribe(kv -> {
            System.out.println(new String(kv.getKey()) + ":" + new String(kv.getValue().getValue()));
        });
    }

    @Test
    void put() {
    }

    @Test
    void doPutAll() {
    }

    @Test
    void remove() {
    }

    @Test
    void doRemoveAll() {
    }

    @Test
    void toStoreKey() {
    }

    @Test
    void getAll() {
    }

    @Test
    void putAll() {
    }

    @Test
    void removeAll() {
    }

    @Test
    void doStoreGet() {
    }

    @Test
    void doStorePut() {
    }

    @Test
    void doStorePutAll() {
    }

    @Test
    void doStoreRemove() {
    }

    @Test
    void doStoreRemoveAll() {
    }

    @Test
    void clear() {
    }
}