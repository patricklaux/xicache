package com.igeeksky.xcache;

/**
 * 生成和管理 CacheManager
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public interface CachingProvider extends Provider {

    CacheManager getCacheManager();

}
