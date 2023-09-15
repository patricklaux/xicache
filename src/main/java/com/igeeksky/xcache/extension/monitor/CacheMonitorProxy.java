package com.igeeksky.xcache.extension.monitor;

import com.igeeksky.xcache.common.CacheLevel;
import com.igeeksky.xcache.common.CacheValue;

import java.util.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-20
 */
public class CacheMonitorProxy<K, V> implements CacheMonitor<K, V> {

    private final List<CacheMonitor<K, V>> cacheMonitors = new ArrayList<>();

    public void addCacheMonitors(Collection<CacheMonitor<K, V>> cacheMonitors) {
        if (null != cacheMonitors) {
            this.cacheMonitors.addAll(cacheMonitors);
        }
    }

    @Override
    public void afterGet(K key, CacheValue<V> cacheValue, CacheLevel cacheLevel) {
        cacheMonitors.forEach(monitor -> monitor.afterGet(key, cacheValue, cacheLevel));
    }

    @Override
    public void afterLoad(K key, V value) {
        cacheMonitors.forEach(monitor -> monitor.afterLoad(key, value));
    }

    @Override
    public void afterPut(K key, V value, CacheLevel cacheLevel) {
        cacheMonitors.forEach(monitor -> monitor.afterPut(key, value, cacheLevel));
    }

    @Override
    public void afterPutAll(Map<? extends K, ? extends V> keyValues, CacheLevel cacheLevel) {
        cacheMonitors.forEach(monitor -> monitor.afterPutAll(keyValues, cacheLevel));
    }

    @Override
    public void afterRemove(K key, CacheLevel cacheLevel) {
        cacheMonitors.forEach(monitor -> monitor.afterRemove(key, cacheLevel));
    }

    @Override
    public void afterRemoveAll(Set<? extends K> keys, CacheLevel cacheLevel) {
        cacheMonitors.forEach(monitor -> monitor.afterRemoveAll(keys, cacheLevel));
    }

    @Override
    public void afterClear(CacheLevel cacheLevel) {
        cacheMonitors.forEach(m -> m.afterClear(cacheLevel));
    }

}