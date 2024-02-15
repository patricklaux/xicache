package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Expiry;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.Provider;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
public interface CaffeineExpiryProvider extends Provider {

    Expiry<String, CacheValue<Object>> get(String name);

}
