package com.igeeksky.xcache.extension.lock;

import com.igeeksky.xcache.config.CacheProps;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-06-10
 */
public class LocalCacheLockProvider implements CacheLockProvider {

    private static final LocalCacheLockProvider INSTANCE = new LocalCacheLockProvider();

    private static final int DEFAULT_LOCK_SIZE = 512;

    public static LocalCacheLockProvider getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public <K> CacheLock<K> get(Class<K> keyType, CacheProps cacheProps) {
        Integer lockSize = cacheProps.getExtension().getCacheLockSize();
        if (lockSize == null) {
            return new LocalCacheLock<>(DEFAULT_LOCK_SIZE);
        }
        return new LocalCacheLock<>(lockSize);
    }

}