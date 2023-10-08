package com.igeeksky.xcache.autoconfigure.holder;

import com.igeeksky.xcache.store.LocalCacheStoreProvider;
import com.igeeksky.xtool.core.lang.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-29
 */
public class LocalCacheStoreProviderHolder implements Holder<LocalCacheStoreProvider> {

    private final Map<String, LocalCacheStoreProvider> map = new HashMap<>();

    @Override
    public void put(String beanId, LocalCacheStoreProvider provider) {
        map.put(beanId, provider);
    }

    @Override
    public LocalCacheStoreProvider get(String beanId) {
        LocalCacheStoreProvider provider = map.get(beanId);
        Assert.notNull(provider, "beanId:[" + beanId + "] CaffeineCacheStoreProvider doesn't exist");
        return provider;
    }

    @Override
    public Map<String, LocalCacheStoreProvider> getAll() {
        return Collections.unmodifiableMap(map);
    }

}
