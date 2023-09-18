package com.igeeksky.xcache.extension.sync;

import com.igeeksky.xcache.common.StoreType;
import com.igeeksky.xcache.common.CacheType;
import com.igeeksky.xcache.extension.CacheMessageConsumer;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.serializer.Serializer;
import com.igeeksky.xcache.store.LocalCacheStore;
import com.igeeksky.xtool.core.collection.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 处理缓存的更新事件
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-11
 */
public class CacheSyncMonitor<V> implements CacheMonitor<V>, CacheMessageConsumer {

    private final String sid;

    /**
     * sync-channel:cache-name
     */
    private final String channel;

    private final CacheType cacheType;

    private final LocalCacheStore localCache;

    private final CacheMessagePublisher publisher;

    private final Serializer<CacheSyncMessage> serializer;

    public CacheSyncMonitor(String sid, String channel, CacheType cacheType,
                            CacheMessagePublisher publisher, LocalCacheStore localCache, Serializer<CacheSyncMessage> serializer) {
        this.sid = sid;
        this.channel = channel;
        this.cacheType = cacheType;
        this.publisher = publisher;
        this.localCache = localCache;
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

    public void onMessage(byte[] source) {
        CacheSyncMessage message = serializer.deserialize(source);
        String sourceId = message.getSid();
        if (Objects.equals(sid, sourceId)) {
            return;
        }
        int type = message.getType();
        if (Objects.equals(CacheSyncMessage.TYPE_REMOVE, type)) {
            Set<String> keys = message.getKeys();
            if (CollectionUtils.isNotEmpty(keys)) {
                localCache.removeAll(keys).subscribe();
            }
            return;
        }
        if (Objects.equals(CacheSyncMessage.TYPE_CLEAR, type)) {
            localCache.clear().subscribe();
        }
    }
}
