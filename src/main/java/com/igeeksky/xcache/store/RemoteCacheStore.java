package com.igeeksky.xcache.store;

import com.igeeksky.xcache.ReactiveCache;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-16
 */
public interface RemoteCacheStore extends ReactiveCache<String, byte[]> {
    // TODO useKeyPrefix
}
