package com.igeeksky.xcache.extension.monitor;

import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.StoreType;

import java.util.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-20
 */
public class CacheMonitorProxy<V> implements CacheMonitor<V> {

    private final List<CacheMonitor<V>> cacheMonitors = new ArrayList<>();

    public void addCacheMonitors(Collection<CacheMonitor<V>> cacheMonitors) {
        if (null != cacheMonitors) {
            this.cacheMonitors.addAll(cacheMonitors);
        }
    }

    @Override
    public void afterGet(String key, CacheValue<V> cacheValue, StoreType storeType) {
        cacheMonitors.forEach(monitor -> monitor.afterGet(key, cacheValue, storeType));
    }

    @Override
    public void afterLoad(String key, V value) {
        cacheMonitors.forEach(monitor -> monitor.afterLoad(key, value));
    }

    @Override
    public void afterPut(String key, V value, StoreType storeType) {
        cacheMonitors.forEach(monitor -> monitor.afterPut(key, value, storeType));
    }

    @Override
    public void afterPutAll(Map<String, ? extends V> keyValues, StoreType storeType) {
        cacheMonitors.forEach(monitor -> monitor.afterPutAll(keyValues, storeType));
    }

    @Override
    public void afterRemove(String key, StoreType storeType) {
        cacheMonitors.forEach(monitor -> monitor.afterRemove(key, storeType));
    }

    @Override
    public void afterRemoveAll(Set<String> keys, StoreType storeType) {
        cacheMonitors.forEach(monitor -> monitor.afterRemoveAll(keys, storeType));
    }

    @Override
    public void afterClear(StoreType storeType) {
        cacheMonitors.forEach(m -> m.afterClear(storeType));
    }

}