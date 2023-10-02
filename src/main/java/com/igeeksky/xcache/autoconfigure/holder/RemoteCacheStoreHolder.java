package com.igeeksky.xcache.autoconfigure.holder;

import com.igeeksky.xcache.autoconfigure.holder.Holder;
import com.igeeksky.xcache.store.RemoteCacheStoreProvider;

import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-30
 */
public interface RemoteCacheStoreHolder extends Holder<RemoteCacheStoreProvider> {

    @Override
    RemoteCacheStoreProvider get(String beanId);

    @Override
    Map<String, RemoteCacheStoreProvider> getAll();

}
