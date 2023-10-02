package com.igeeksky.xcache.autoconfigure.lettuce;

import com.igeeksky.xcache.autoconfigure.holder.RemoteCacheStoreHolder;
import com.igeeksky.xcache.store.RemoteCacheStoreProvider;
import com.igeeksky.xcache.extension.redis.RedisCacheStoreProvider;
import com.igeeksky.xtool.core.lang.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-29
 */
public class LettuceCacheStoreProviderHolder implements RemoteCacheStoreHolder {

    private final Map<String, RedisCacheStoreProvider> map = new HashMap<>();

    public LettuceCacheStoreProviderHolder(Map<String, RedisCacheStoreProvider> map) {
        Assert.notNull(map, "LettuceCacheStoreProvider map must not be null");
        this.map.putAll(map);
    }

    @Override
    public RedisCacheStoreProvider get(String beanId) {
        RedisCacheStoreProvider provider = map.get(beanId);
        Assert.notNull(provider, "beanId:[" + beanId + "] LettuceCacheStoreProvider doesn't exit");
        return provider;
    }

    @Override
    public Map<String, RemoteCacheStoreProvider> getAll() {
        return Collections.unmodifiableMap(map);
    }
}
