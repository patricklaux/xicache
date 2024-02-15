package com.igeeksky.xcache.store;

import com.igeeksky.xcache.ReactiveCache;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-14
 */
public interface Store<K, V> extends ReactiveCache<K, V> {

    String getStoreName();

}
