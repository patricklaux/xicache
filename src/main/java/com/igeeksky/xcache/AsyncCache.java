package com.igeeksky.xcache;

import com.igeeksky.xcache.extension.loader.CacheLoader;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xtool.core.lang.Assert;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 异步缓存
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-03
 */
public interface AsyncCache<K, V> {

    CompletableFuture<CacheValue<V>> get(K key);

    CompletableFuture<CacheValue<V>> get(K key, CacheLoader<K, V> loader);

    CompletableFuture<Map<K, CacheValue<V>>> getAll(Set<? extends K> keys);

    CompletableFuture<Void> putAll(CompletableFuture<Map<? extends K, ? extends V>> keyValues);

    CompletableFuture<Void> put(K key, CompletableFuture<V> value);

    CompletableFuture<Void> remove(K key);

    CompletableFuture<Void> removeAll(Set<? extends K> keys);

    CompletableFuture<Void> clear();

    /**
     * 异步缓存视图
     *
     * @param <K> key
     * @param <V> value
     */
    class AsyncCacheView<K, V> implements AsyncCache<K, V> {

        private final Cache<K, V> cache;

        public AsyncCacheView(Cache<K, V> cache) {
            Assert.notNull(cache, "Cache must not be null.");
            this.cache = cache;
        }

        @Override
        public CompletableFuture<CacheValue<V>> get(K key) {
            return cache.get(key).toFuture();
        }

        @Override
        public CompletableFuture<CacheValue<V>> get(K key, CacheLoader<K, V> loader) {
            return cache.get(key, loader).toFuture();
        }

        @Override
        public CompletableFuture<Map<K, CacheValue<V>>> getAll(Set<? extends K> keys) {
            return cache.getAll(keys).collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue, (k, v) -> v)).toFuture();
        }

        @Override
        public CompletableFuture<Void> putAll(CompletableFuture<Map<? extends K, ? extends V>> keyValues) {
            return cache.putAll(Mono.fromFuture(keyValues)).toFuture();
        }

        @Override
        public CompletableFuture<Void> put(K key, CompletableFuture<V> value) {
            return cache.put(key, Mono.fromFuture(value)).toFuture();
        }

        @Override
        public CompletableFuture<Void> remove(K key) {
            return cache.remove(key).toFuture();
        }

        @Override
        public CompletableFuture<Void> removeAll(Set<? extends K> keys) {
            return cache.removeAll(keys).toFuture();
        }

        @Override
        public CompletableFuture<Void> clear() {
            return cache.clear().toFuture();
        }
    }

}
