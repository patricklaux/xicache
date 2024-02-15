package com.igeeksky.xcache.extension.monitor;


import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.StoreType;

import java.util.Map;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-06
 */
public interface CacheMonitor<V> {

    default void afterGet(String key, CacheValue<V> cacheValue, StoreType storeType) {
    }

    default void afterPut(String key, V value, StoreType storeType) {
    }

    default void afterPutAll(Map<String, ? extends V> keyValues, StoreType storeType) {
    }

    default void afterLoad(String key, V value) {
    }

    default void afterRemove(String key, StoreType storeType) {
    }

    default void afterRemoveAll(Set<String> keys, StoreType storeType) {
    }

    default void afterClear(StoreType storeType) {
    }

}
