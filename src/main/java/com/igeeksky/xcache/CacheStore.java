package com.igeeksky.xcache;

import com.igeeksky.xcache.common.StoreType;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-14
 */
public interface CacheStore<K, V> extends ReactiveCache<K, V> {
    
    StoreType getStoreType();

}
