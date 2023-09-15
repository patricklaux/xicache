package com.igeeksky.xcache;

import java.util.Collection;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-10
 */
public interface CacheManager {

    <K, V> Cache<K, V> get(String name, Class<K> keyType, Class<V> valueType);

    Collection<Cache<?, ?>> getAll();

    Collection<String> getAllCacheNames();

}
