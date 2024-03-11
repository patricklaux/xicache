package com.igeeksky.xcache;

import com.igeeksky.xcache.config.props.CacheProps;
import com.igeeksky.xcache.config.props.TemplateId;
import com.igeeksky.xcache.extension.compress.GzipCompressorProvider;
import com.igeeksky.xcache.extension.contains.TrueContainsPredicateProvider;
import com.igeeksky.xcache.extension.lock.LocalCacheLockProvider;
import com.igeeksky.xcache.extension.statistic.LogCacheStatManager;
import com.igeeksky.xcache.support.caffeine.CaffeineStoreProvider;
import com.igeeksky.xcache.support.jackson.JacksonKeyConvertorProvider;
import com.igeeksky.xcache.support.jackson.JacksonSerializerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-24
 */
class XcacheManagerTest {

    Cache<String, User> cache;
    ReCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Hooks.onOperatorDebug();

        String name = "user";
        String application = "shop";
        CacheProps t0 = new CacheProps();
        t0.setCacheType("local");
        // t0.getLocal().setCacheStore("caffeineCacheStoreProvider");
        // t0.getLocal().setEnableNullValue(true);
        t0.getLocal().setEnableRandomTtl(true);
        t0.getLocal().setExpireAfterWrite(10000L);
        t0.getLocal().setExpireAfterAccess(2000L);
        t0.getLocal().setValueSerializer("jacksonSerializerProvider");
        t0.getLocal().setValueCompressor("gzipCompressorProvider");
        // t0.getExtension().setCacheStat("logCacheStatManager");
        t0.getExtension().setCacheSync("none");

        Map<TemplateId, CacheProps> templatesMap = new HashMap<>();
        templatesMap.put(TemplateId.T0, t0);

        Map<String, CacheProps> propsMap = new HashMap<>();
        // propsMap.put(name, t0);

        cacheManager = new ReCacheManager(application, templatesMap, propsMap);

        // cacheManager.addProvider("lettuceCacheSyncManager", new LettuceCacheSyncManager());
        // cacheManager.addProvider("cacheMonitorProvider", new CacheMonitorProvider());
        // cacheManager.addProvider("lettuceCacheStoreProvider", new LettuceCacheStoreProvider());

        cacheManager.addProvider("jacksonKeyConvertorProvider", new JacksonKeyConvertorProvider());
        cacheManager.addProvider("jacksonSerializerProvider", new JacksonSerializerProvider());
        cacheManager.addProvider("gzipCompressorProvider", new GzipCompressorProvider());
        cacheManager.addProvider("logCacheStatManager", new LogCacheStatManager(10000));
        cacheManager.addProvider("alwaysTruePredicateProvider", TrueContainsPredicateProvider.INSTANCE);
        cacheManager.addProvider("localCacheLockProvider", LocalCacheLockProvider.getInstance());
        cacheManager.addProvider("caffeineCacheStoreProvider", new CaffeineStoreProvider(null, null));

        cache = cacheManager.getOrCreateCache(name, String.class, User.class, null);
    }

    @AfterEach
    void tearDown() {
        Hooks.resetOnOperatorDebug();
    }

    @Test
    void testGet() throws InterruptedException {
        User jack = new User("001", "Jack", 16);
        cache.put("001", Mono.just(jack)).subscribe();
        cache.get("001")
                .doOnSuccess(cv -> {
                    System.out.println(1 + ":" + cv.getValue());
                    Assertions.assertNotNull(cv);
                    Assertions.assertNotNull(cv.getValue());
                    Assertions.assertEquals(cv.getValue(), jack);
                }).subscribe();
        Thread.sleep(5000L);
        cache.get("001")
                .doOnSuccess(cv -> {
                    System.out.println(2 + ":" + cv.getValue());
                    Assertions.assertNotNull(cv);
                    Assertions.assertNotNull(cv.getValue());
                    Assertions.assertEquals(cv.getValue(), jack);
                }).subscribe();
        Thread.sleep(5000L);
        cache.get("001")
                .doOnSuccess(cv -> {
                    System.out.println(3 + ":" + cv);
                    Assertions.assertNull(cv);
                }).subscribe();
    }

    @Test
    void testPut() throws InterruptedException {
        cache.put("001", Mono.empty()).subscribe();
    }

    @Test
    void getOrCreateCache() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getAllCacheNames() {
    }
}