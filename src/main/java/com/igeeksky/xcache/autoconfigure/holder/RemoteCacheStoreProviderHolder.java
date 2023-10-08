package com.igeeksky.xcache.autoconfigure.holder;

import com.igeeksky.xcache.store.RemoteCacheStoreProvider;
import com.igeeksky.xtool.core.lang.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-29
 */
public class RemoteCacheStoreProviderHolder implements Holder<RemoteCacheStoreProvider> {

    private final Map<String, RemoteCacheStoreProvider> map = new HashMap<>();

    @Override
    public void put(String beanId, RemoteCacheStoreProvider provider) {
        map.put(beanId, provider);
    }

    @Override
    public RemoteCacheStoreProvider get(String beanId) {
        RemoteCacheStoreProvider provider = map.get(beanId);
        Assert.notNull(provider, "beanId:[" + beanId + "] RedisCacheStoreProvider doesn't exist");
        return provider;
    }

    @Override
    public Map<String, RemoteCacheStoreProvider> getAll() {
        return Collections.unmodifiableMap(map);
    }
}
