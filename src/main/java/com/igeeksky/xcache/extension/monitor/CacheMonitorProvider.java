package com.igeeksky.xcache.extension.monitor;


import com.igeeksky.xcache.common.Provider;
import com.igeeksky.xcache.config.props.CacheProps;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-06
 */
public interface CacheMonitorProvider extends Provider {

    <K, V> CacheMonitor<V> get(CacheProps cacheProps, Class<K> keyType, Class<V> valueType);

}
