package com.igeeksky.xcache.config;

import com.igeeksky.xcache.config.props.TemplateId;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public interface CacheConstants {

    /**
     * <p>缓存数据同步广播的通道名的中缀</p>
     * 完整通道名：配置项（cache-sync-channel） + ":sync:" + 配置项（cache-name）
     */
    String SYNC_CHANNEL_INFIX = ":sync:";

    /**
     * <p>缓存数据统计信息的队列名的中缀</p>
     * 完整通道名：配置项（cache-stat-channel） + ":stat:" + 配置项（cache-name）
     */
    String STAT_CHANNEL_INFIX = ":stat:";

    /**
     * 停用某个 String 类型的配置项，可以配置为 none
     */
    String NONE = "NONE";
    TemplateId DEFAULT_TEMPLATE = TemplateId.T0;
    String DEFAULT_CHARSET_NAME = "UTF-8";
    String DEFAULT_CACHE_TYPE = "both";

    // 本地缓存默认配置 start
    String LOCAL_CACHE_STORE = "caffeineCacheStoreProvider";
    String LOCAL_STORE_NAME = "caffeine";
    int LOCAL_INITIAL_CAPACITY = 1024;
    long LOCAL_MAXIMUM_SIZE = 2048L;
    long LOCAL_MAXIMUM_WEIGHT = 0L;
    long LOCAL_EXPIRE_AFTER_WRITE = 3600000L;
    long LOCAL_EXPIRE_AFTER_ACCESS = 360000L;
    String LOCAL_KEY_STRENGTH = NONE;
    String LOCAL_VALUE_STRENGTH = NONE;
    String LOCAL_VALUE_SERIALIZER = NONE;
    String LOCAL_VALUE_COMPRESSOR = NONE;
    boolean LOCAL_ENABLE_RANDOM_TTL = true;
    boolean LOCAL_ENABLE_NULL_VALUE = true;
    // 本地缓存默认配置 end


    // 远程缓存默认配置 start
    String REMOTE_CACHE_STORE = "lettuceCacheStoreProvider";
    String REMOTE_STORE_NAME = "redis-string";
    long REMOTE_EXPIRE_AFTER_WRITE = 7200000L;
    String REMOTE_VALUE_SERIALIZER = "jacksonSerializerProvider";
    String REMOTE_VALUE_COMPRESSOR = NONE;
    boolean REMOTE_ENABLE_KEY_PREFIX = true;
    boolean REMOTE_ENABLE_RANDOM_TTL = true;
    boolean REMOTE_ENABLE_NULL_VALUE = true;
    // 远程缓存默认配置 end


    // 扩展属性默认配置 start
    String EXTENSION_KEY_CONVERTOR = "jacksonKeyConvertorProvider";
    String EXTENSION_CACHE_LOCK = "localCacheLockProvider";
    int EXTENSION_CACHE_LOCK_SIZE = 128;
    String EXTENSION_CONTAINS_PREDICATE = "alwaysTruePredicateProvider";
    String EXTENSION_CACHE_SYNC = "lettuceCacheSyncManager";
    String EXTENSION_CACHE_SYNC_CHANNEL = NONE;
    String EXTENSION_CACHE_SYNC_SERIALIZER = "jacksonSerializerProvider";
    String EXTENSION_CACHE_STAT = "logCacheStatManager";
    String EXTENSION_CACHE_LOADER = NONE;
    String EXTENSION_CACHE_MONITORS = NONE;
    // 扩展属性默认配置 end

}
