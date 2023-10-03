package com.igeeksky.xcache.autoconfigure.redis.lettuce;

import com.igeeksky.xcache.autoconfigure.holder.Holder;
import com.igeeksky.xcache.extension.redis.RedisConnectionFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-29
 */
public class RedisConnectionFactoryHolder implements Holder<RedisConnectionFactory> {

    private final Map<String, RedisConnectionFactory> map = new HashMap<>();

    public RedisConnectionFactoryHolder(Map<String, RedisConnectionFactory> factoryMap) {
        this.map.putAll(factoryMap);
    }

    @Override
    public RedisConnectionFactory get(String beanId) {
        return map.get(beanId);
    }

    @Override
    public Map<String, RedisConnectionFactory> getAll() {
        return Collections.unmodifiableMap(map);
    }

}
