package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Expiry;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.Provider;
import com.igeeksky.xcache.config.CacheConfig;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
public interface CaffeineExpiryProvider extends Provider {

    <K, V> Expiry<String, CacheValue<Object>> get(CacheConfig<K, V> cacheConfig);

}
