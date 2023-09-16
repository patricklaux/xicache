package com.igeeksky.xcache.extension.statistic;

import com.igeeksky.xcache.common.CacheLevel;
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

    private final AtomicReference<CacheStatCounter> refL1 = new AtomicReference<>(new CacheStatCounter());
    private final AtomicReference<CacheStatCounter> refL2 = new AtomicReference<>(new CacheStatCounter());

    public CacheStatMonitor(String name, String application) {
        this.name = name;
        this.application = application;
    }

    @Override
    public void afterGet(String key, CacheValue<V> cacheValue, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            if (null == cacheValue) {
                refL1.get().incMisses();
            } else {
                refL1.get().incHits();
            }
        } else {
            if (null == cacheValue) {
                refL1.get().incMisses();
            } else {
                refL2.get().incHits();
            }
        }
    }

    @Override
    public void afterPut(String key, V value, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refL1.get().incPuts(1);
        } else {
            refL2.get().incPuts(1);
        }
    }

    @Override
    public void afterPutAll(Map<String, ? extends V> keyValues, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refL1.get().incPuts(keyValues.size());
        } else {
            refL2.get().incPuts(keyValues.size());
        }
    }

    @Override
    public void afterLoad(String key, V value) {
        refL1.get().incLoads();
    }

    @Override
    public void afterRemove(String key, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refL1.get().incRemovals(1);
        } else {
            refL2.get().incRemovals(1);
        }
    }

    @Override
    public void afterRemoveAll(Set<String> keys, StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refL1.get().incRemovals(keys.size());
        } else {
            refL2.get().incRemovals(keys.size());
        }
    }

    @Override
    public void afterClear(StoreType storeType) {
        if (StoreType.LOCAL == storeType) {
            refL1.get().incClears();
        } else {
            refL2.get().incClears();
        }
    }

    public Tuple2<CacheStatMessage, CacheStatMessage> collect() {
        CacheStatCounter cntL1 = refL1.getAndSet(new CacheStatCounter());
        CacheStatCounter cntL2 = refL2.getAndSet(new CacheStatCounter());
        CacheStatMessage msgL1 = new CacheStatMessage(name, application, CacheLevel.L1, cntL1);
        CacheStatMessage msgL2 = new CacheStatMessage(name, application, CacheLevel.L2, cntL2);
        return Tuples.of(msgL1, msgL2);
    }
}
