package com.igeeksky.xcache.extension.monitor;


import com.igeeksky.xcache.common.CacheLevel;
import com.igeeksky.xcache.common.CacheValue;

import java.util.Map;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-06
 */
public interface CacheMonitor<K, V> {

    default void afterGet(K key, CacheValue<V> cacheValue, CacheLevel cacheLevel) {
    }

    default void afterPut(K key, V value, CacheLevel cacheLevel) {
    }

    default void afterPutAll(Map<? extends K, ? extends V> keyValues, CacheLevel cacheLevel) {
    }

    default void afterLoad(K key, V value) {
    }

    default void afterRemove(K key, CacheLevel cacheLevel) {
    }

    default void afterRemoveAll(Set<? extends K> keys, CacheLevel cacheLevel) {
    }

    default void afterClear(CacheLevel cacheLevel) {
    }

}
