package com.igeeksky.xcache.autoconfigure.redis;

import com.igeeksky.xcache.autoconfigure.holder.Holder;
import com.igeeksky.xcache.extension.redis.RedisCacheSyncManager;
import com.igeeksky.xtool.core.lang.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-02
 */
public class RedisCacheSyncProviderHolder implements Holder<RedisCacheSyncManager> {

    private final Map<String, RedisCacheSyncManager> map = new HashMap<>();

    public RedisCacheSyncProviderHolder(Map<String, RedisCacheSyncManager> map) {
        Assert.notNull(map, "RedisCacheSyncManager map must not be null");
        this.map.putAll(map);
    }

    @Override
    public RedisCacheSyncManager get(String beanId) {
        RedisCacheSyncManager manager = map.get(beanId);
        Assert.notNull(manager, "beanId:[" + beanId + "] RedisCacheSyncManager doesn't exit");
        return manager;
    }

    @Override
    public Map<String, RedisCacheSyncManager> getAll() {
        return Collections.unmodifiableMap(map);
    }

}
