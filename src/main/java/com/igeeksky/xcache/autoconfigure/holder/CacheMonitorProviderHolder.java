package com.igeeksky.xcache.autoconfigure.holder;

import com.igeeksky.xcache.extension.monitor.CacheMonitorProvider;
import com.igeeksky.xtool.core.lang.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-02
 */
public class CacheMonitorProviderHolder implements Holder<CacheMonitorProvider> {

    private final Map<String, CacheMonitorProvider> map = new HashMap<>();

    @Override
    public void put(String beanId, CacheMonitorProvider provider) {
        map.put(beanId, provider);
    }

    @Override
    public CacheMonitorProvider get(String beanId) {
        CacheMonitorProvider provider = map.get(beanId);
        Assert.notNull(provider, "beanId:[" + beanId + "] CacheMonitorProvider doesn't exit");
        return provider;
    }

    @Override
    public Map<String, CacheMonitorProvider> getAll() {
        return Collections.unmodifiableMap(map);
    }

}
