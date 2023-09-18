package com.igeeksky.xcache.extension.statistic;

import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.StoreType;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 缓存统计指标
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-11
 */
public class CacheStatMonitor<V> implements CacheMonitor<V> {

    private final String name;
    private final String application;

    private final AtomicReference<CacheStatCounter> refNone = new AtomicReference<>(new CacheStatCounter());
    private final AtomicReference<CacheStatCounter> refLocal = new AtomicReference<>(new CacheStatCounter());
    private final AtomicReference<CacheStatCounter> refRemote = new AtomicReference<>(new CacheStatCounter());

    public CacheStatMonitor(String name, String application) {
        this.name = name;
        this.application = application;
    }

    @Override
    public void afterGet(String key, CacheValue<V> cacheValue, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            if (null == cacheValue) {
                refLocal.get().incMisses();
            } else {
                refLocal.get().incHits();
            }
        } else if (StoreType.REMOTE == storeType) {
            if (null == cacheValue) {
                refLocal.get().incMisses();
            } else {
                refRemote.get().incHits();
            }
        } else {
            refNone.get().incMisses();
        }
    }

    @Override
    public void afterPut(String key, V value, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refLocal.get().incPuts(1);
        } else if (StoreType.REMOTE == storeType) {
            refRemote.get().incPuts(1);
        } else {
            refNone.get().incPuts(1);
        }
    }

    @Override
    public void afterPutAll(Map<String, ? extends V> keyValues, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refLocal.get().incPuts(keyValues.size());
        } else if (StoreType.REMOTE == storeType) {
            refRemote.get().incPuts(keyValues.size());
        } else {
            refNone.get().incPuts(keyValues.size());
        }
    }

    @Override
    public void afterLoad(String key, V value) {
        refLocal.get().incLoads();
    }

    @Override
    public void afterRemove(String key, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refLocal.get().incRemovals(1);
        } else if (StoreType.REMOTE == storeType) {
            refRemote.get().incRemovals(1);
        } else {
            refNone.get().incRemovals(1);
        }
    }

    @Override
    public void afterRemoveAll(Set<String> keys, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refLocal.get().incRemovals(keys.size());
        } else if (StoreType.REMOTE == storeType) {
            refRemote.get().incRemovals(keys.size());
        } else {
            refNone.get().incRemovals(keys.size());
        }
    }

    @Override
    public void afterClear(StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refLocal.get().incClears();
        } else if (StoreType.REMOTE == storeType) {
            refRemote.get().incClears();
        } else {
            refNone.get().incClears();
        }
    }

    public Tuple2<CacheStatMessage, CacheStatMessage> collect() {
        CacheStatCounter localCounter = refLocal.getAndSet(new CacheStatCounter());
        CacheStatCounter remoteCounter = refRemote.getAndSet(new CacheStatCounter());
        CacheStatMessage localStatMsg = new CacheStatMessage(name, application, StoreType.LOCAL, localCounter);
        CacheStatMessage remoteStatMsg = new CacheStatMessage(name, application, StoreType.REMOTE, remoteCounter);
        return Tuples.of(localStatMsg, remoteStatMsg);
    }
}
