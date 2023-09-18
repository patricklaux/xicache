package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Weigher;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.config.CacheConfig;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
public interface CaffeineWeigherProvider {

    <K, V> Weigher<String, CacheValue<Object>> get(CacheConfig<K, V> cacheConfig);

}
