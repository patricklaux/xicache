package com.igeeksky.xcache.config;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public interface CacheConstants {

    /**
     * 无需配置
     */
    String NONE = "none";

    // 本地缓存默认配置 start
    String LOCAL_CACHE_STORE = "caffeineCacheStoreProvider";

    // 本地缓存默认配置 end


    // 远程缓存默认配置 start
    String REMOTE_CACHE_STORE = "lettuceCacheStoreProvider";
    String REMOTE_STORE_NAME = "redis-string";
    long REMOTE_EXPIRE_AFTER_WRITE = 3600000L;
    String REMOTE_KEY_CONVERTOR = "jacksonKeyConvertorProvider";
    String REMOTE_VALUE_SERIALIZER = "jacksonSerializerProvider";
    String REMOTE_VALUE_COMPRESSOR = "gzipCompressorProvider";
    boolean REMOTE_ENABLE_KEY_PREFIX = true;
    boolean REMOTE_ENABLE_NULL_VALUE = true;
    boolean REMOTE_ENABLE_COMPRESS_VALUE = false;
    // 远程缓存默认配置 end

    // 扩展属性默认配置 start
    String EXTENSION_CACHE_LOCK = "localCacheLockProvider";

    // 扩展属性默认配置 end

}
