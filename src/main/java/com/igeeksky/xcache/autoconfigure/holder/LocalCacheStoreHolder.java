package com.igeeksky.xcache.autoconfigure.holder;

import com.igeeksky.xcache.store.LocalCacheStoreProvider;

import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-30
 */
public interface LocalCacheStoreHolder extends Holder<LocalCacheStoreProvider> {

    @Override
    LocalCacheStoreProvider get(String beanId);

    @Override
    Map<String, LocalCacheStoreProvider> getAll();

}
