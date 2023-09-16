package com.igeeksky.xcache.extension.monitor;


import com.igeeksky.xcache.common.Provider;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-06
 */
public interface CacheMonitorProvider extends Provider {

    <K, V> CacheMonitor<V> get(String name, Class<K> keyType, Class<V> valueType);

}
