package com.igeeksky.xcache.extension.sync;

import com.igeeksky.xcache.common.CacheType;
import com.igeeksky.xcache.common.StoreType;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.serializer.Serializer;

import java.util.Map;
import java.util.Set;

/**
 * 处理缓存的更新事件
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-11
 */
public class CacheSyncMonitor<V> implements CacheMonitor<V> {

    private final String sid;

    /**
     * sync-channel:cache-name
     */
    private final String channel;

    private final CacheType cacheType;

    private final CacheMessagePublisher publisher;

    private final Serializer<CacheSyncMessage> serializer;

    public CacheSyncMonitor(String sid, String channel, CacheType cacheType,
                            CacheMessagePublisher publisher, Serializer<CacheSyncMessage> serializer) {
        this.sid = sid;
        this.channel = channel;
        this.cacheType = cacheType;
        this.publisher = publisher;
        this.serializer = serializer;
    }

    @Override
    public void afterPut(String key, V value, StoreType storeType) {
        if (CacheType.BOTH == cacheType) {
            sendMessage(new CacheSyncMessage(sid).setType(CacheSyncMessage.TYPE_REMOVE).addKey(key));
        }
    }

    @Override
    public void afterPutAll(Map<String, ? extends V> keyValues, StoreType storeType) {
        if (CacheType.BOTH == cacheType) {
            sendMessage(new CacheSyncMessage(sid).setType(CacheSyncMessage.TYPE_REMOVE).setKeys(keyValues.keySet()));
        }
    }

    @Override
    public void afterLoad(String key, V value) {
        if (CacheType.BOTH == cacheType) {
            sendMessage(new CacheSyncMessage(sid).setType(CacheSyncMessage.TYPE_REMOVE).addKey(key));
        }
    }

    @Override
    public void afterRemove(String key, StoreType storeType) {
        if (CacheType.BOTH == cacheType) {
            sendMessage(new CacheSyncMessage(sid).setType(CacheSyncMessage.TYPE_REMOVE).addKey(key));
        }
    }

    @Override
    public void afterRemoveAll(Set<String> keys, StoreType storeType) {
        if (CacheType.BOTH == cacheType) {
            sendMessage(new CacheSyncMessage(sid).setType(CacheSyncMessage.TYPE_REMOVE).setKeys(keys));
        }
    }

    @Override
    public void afterClear(StoreType storeType) {
        if (CacheType.BOTH == cacheType || CacheType.LOCAL == cacheType) {
            sendMessage(new CacheSyncMessage(sid).setType(CacheSyncMessage.TYPE_CLEAR));
        }
    }

    private void sendMessage(CacheSyncMessage message) {
        publisher.publish(channel, serializer.serialize(message));
    }

}
