package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import com.igeeksky.xcache.common.CacheValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Hooks;
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
class CaffeineStoreTest {

    private CaffeineStore<String, Object> cache;

    @BeforeEach
    void setUp() {
        Hooks.onOperatorDebug();
        Cache<String, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .initialCapacity(128)
                .maximumSize(1024)
                .build();
        cache = new CaffeineStore<>(caffeine);
        Hooks.resetOnOperatorDebug();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void get() throws InterruptedException {
        cache.put("a", Mono.just("a")).subscribe();
        Thread.sleep(2880);
        Mono<CacheValue<Object>> mono = cache.get("a");
        mono.filter(Objects::nonNull).doOnNext(cv -> System.out.println(cv.getValue())).subscribe();
    }

    @Test
    void getBytes() throws InterruptedException {
        Cache<String, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
        CaffeineStore<String, byte[]> cache = new CaffeineStore<>(caffeine);

        String str = "hash";
        byte[] source = str.getBytes(StandardCharsets.UTF_8);
        cache.put(str, Mono.just(source)).subscribe();

        cache.get(str).subscribe(cacheValue -> {
            Assertions.assertNotNull(cacheValue);
            Assertions.assertNotNull(cacheValue.getValue());
            System.out.println(new String((byte[]) cacheValue.getValue()));
        });
    }

    @Test
    void testWeigher() {
        Cache<String, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumWeight(100)
                .weigher(Weigher.singletonWeigher())
                .build();
        CaffeineStore<String, byte[]> cache = new CaffeineStore<>(caffeine);

        String str = "hash";
        byte[] source = str.getBytes(StandardCharsets.UTF_8);
        cache.put(str, Mono.just(source)).subscribe();

        Set<String> set = new HashSet<>();
        set.add(str);
        cache.getAll(set).subscribe(kv -> System.out.println(kv.getKey() + ":" + new String((byte[]) kv.getValue().getValue())));
    }

    @Test
    void getBytes2() throws InterruptedException {
        Cache<String, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
        CaffeineStore<String, byte[]> cache = new CaffeineStore<>(caffeine);

        String str = "hash";
        byte[] source = str.getBytes(StandardCharsets.UTF_8);
        this.cache.put(str, Mono.just(source)).subscribe();
        Assertions.assertNotNull(this.cache.get(str).block());
    }

    @Test
    void doGetAll() {
        Cache<String, CacheValue<Object>> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .maximumSize(1024)
                .build();
        CaffeineStore<String, byte[]> cache = new CaffeineStore<>(caffeine);

        String str = "hash";
        byte[] source = str.getBytes(StandardCharsets.UTF_8);
        cache.put(str, Mono.just(source)).subscribe();

        Set<String> set = new HashSet<>();
        set.add(str);
        cache.getAll(set).subscribe(kv -> System.out.println(kv.getKey() + ":" + new String((byte[]) kv.getValue().getValue())));
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