package com.igeeksky.xcache.extension.sync;

import com.igeeksky.xcache.ReactiveCache;
import com.igeeksky.xcache.common.CacheLevel;
import com.igeeksky.xcache.config.CacheType;
import com.igeeksky.xcache.extension.CacheMessageConsumer;
import com.igeeksky.xcache.extension.serializer.Serializer;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
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
@SuppressWarnings("unchecked")
public class CacheSyncMonitor<K, V> implements CacheMonitor<K, V>, CacheMessageConsumer {

    private final String sid;

    /**
     * sync-channel:cache-name
     */
    private final String channel;

    private final CacheType cacheType;

    private final ReactiveCache<K, V> localCache;

    private final CacheMessagePublisher publisher;

    private final Serializer<CacheSyncMessage<K>> serializer;

    public CacheSyncMonitor(String sid, String channel, CacheType cacheType,
                            CacheMessagePublisher publisher, ReactiveCache<K, V> localCache, Serializer<CacheSyncMessage<K>> serializer) {
        this.sid = sid;
        this.channel = channel;
        this.cacheType = cacheType;
        this.publisher = publisher;
        this.localCache = localCache;
        this.serializer = serializer;
    }

    @Override
    public void afterPut(K key, V value, CacheLevel cacheLevel) {
        if (cacheType == CacheType.BOTH) {
            sendMessage(new CacheSyncMessage<K>(sid).setType(CacheSyncMessage.TYPE_REMOVE).addKey(key));
        }
    }

    @Override
    public void afterPutAll(Map<? extends K, ? extends V> keyValues, CacheLevel cacheLevel) {
        if (cacheType == CacheType.BOTH) {
            sendMessage(new CacheSyncMessage<K>(sid).setType(CacheSyncMessage.TYPE_REMOVE).setKeys((Set<K>) keyValues.keySet()));
        }
    }

    @Override
    public void afterLoad(K key, V value) {
        if (cacheType == CacheType.BOTH) {
            sendMessage(new CacheSyncMessage<K>(sid).setType(CacheSyncMessage.TYPE_REMOVE).addKey(key));
        }
    }

    @Override
    public void afterRemove(K key, CacheLevel cacheLevel) {
        if (cacheType == CacheType.BOTH) {
            sendMessage(new CacheSyncMessage<K>(sid).setType(CacheSyncMessage.TYPE_REMOVE).addKey(key));
        }
    }

    @Override
    public void afterRemoveAll(Set<? extends K> keys, CacheLevel cacheLevel) {
        if (cacheType == CacheType.BOTH) {
            sendMessage(new CacheSyncMessage<K>(sid).setType(CacheSyncMessage.TYPE_REMOVE).setKeys((Set<K>) keys));
        }
    }

    @Override
    public void afterClear(CacheLevel cacheLevel) {
        if (cacheType == CacheType.BOTH || cacheType == CacheType.LOCAL) {
            sendMessage(new CacheSyncMessage<K>(sid).setType(CacheSyncMessage.TYPE_CLEAR));
        }
    }

    private void sendMessage(CacheSyncMessage<K> message) {
        publisher.publish(channel, serializer.serialize(message));
    }

    public void onMessage(byte[] source) {
        CacheSyncMessage<K> message = serializer.deserialize(source);
        String sourceId = message.getSid();
        if (Objects.equals(sid, sourceId)) {
            return;
        }
        int type = message.getType();
        if (Objects.equals(CacheSyncMessage.TYPE_REMOVE, type)) {
            Set<? extends K> keys = message.getKeys();
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
