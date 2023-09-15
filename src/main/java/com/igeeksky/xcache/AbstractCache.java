package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheKeyNullException;
import com.igeeksky.xcache.common.CacheLoader;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xtool.core.collection.Maps;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-19
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    private final String name;
    private final Class<K> keyType;
    private final Class<V> valueType;

    private final Object lock = new Object();
    private volatile SyncCache<K, V> syncCache;
    private volatile AsyncCache<K, V> asyncCache;

    private final CacheLock<K> cacheLock;
    private final ContainsPredicate<K> containsPredicate;

    public AbstractCache(CacheConfig<K, V> config) {
        this.name = config.getName();
        this.keyType = config.getKeyType();
        this.valueType = config.getValueType();
        this.cacheLock = config.getCacheLock();
        this.containsPredicate = config.getContainsPredicate();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<K> getKeyType() {
        return keyType;
    }

    @Override
    public Class<V> getValueType() {
        return valueType;
    }

    @Override
    public Mono<CacheValue<V>> get(K key) {
        if (null == key) {
            return Mono.error(new CacheKeyNullException());
        }
        return this.doGet(key);
    }

    protected abstract Mono<CacheValue<V>> doGet(K key);

    @Override
    public Mono<CacheValue<V>> get(K key, CacheLoader<K, V> cacheLoader) {
        return this.get(key).switchIfEmpty(loadWithLock(key, cacheLoader));
    }

    private Mono<CacheValue<V>> loadWithLock(K key, CacheLoader<K, V> cacheLoader) {
        if (containsPredicate.test(getName(), key)) {
            Lock keyLock = cacheLock.get(key);
            return Mono.just(key)
                    .doOnNext(k -> keyLock.lock())
                    .flatMap(this::get)
                    .switchIfEmpty(this.doGet(key, cacheLoader))
                    .doFinally(s -> keyLock.unlock());
        }
        return Mono.empty();
    }

    protected abstract Mono<CacheValue<V>> doGet(K key, CacheLoader<K, V> cacheLoader);

    @Override
    public Flux<KeyValue<K, CacheValue<V>>> getAll(Set<? extends K> keys) {
        if (null == keys) {
            return Flux.error(new CacheKeyNullException("keys must not be null."));
        }
        if (keys.isEmpty()) {
            return Flux.empty();
        }
        return Mono.just(keys)
                .doOnNext(ks -> ks.forEach(key -> {
                    if (null == key) {
                        throw new CacheKeyNullException();
                    }
                }))
                .flatMapMany(ks -> this.doGetAll(ks).filter(KeyValue::hasValue));
    }

    protected abstract Flux<KeyValue<K, CacheValue<V>>> doGetAll(Set<? extends K> keys);

    @Override
    public Mono<Void> putAll(Mono<Map<? extends K, ? extends V>> keyValues) {
        return keyValues
                .filter(Maps::isNotEmpty)
                .doOnNext(kvs -> {
                    kvs.forEach((k, v) -> {
                        if (null == k) {
                            throw new CacheKeyNullException();
                        }
                    });
                })
                .flatMap(this::doPutAll);
    }

    protected abstract Mono<Void> doPutAll(Map<? extends K, ? extends V> keyValues);

    @Override
    public Mono<Void> put(K key, Mono<V> monoValue) {
        if (null == key) {
            return Mono.error(new CacheKeyNullException());
        }
        return monoValue
                .flatMap(value -> this.doPut(key, value));
    }

    protected abstract Mono<Void> doPut(K key, V value);

    @Override
    public Mono<Void> remove(K key) {
        if (null == key) {
            return Mono.error(new CacheKeyNullException());
        }
        return this.doRemove(key);
    }

    protected abstract Mono<Void> doRemove(K key);

    @Override
    public Mono<Void> removeAll(Set<? extends K> keys) {
        if (null == keys) {
            return Mono.error(new CacheKeyNullException());
        }
        if (keys.isEmpty()) {
            return Mono.empty();
        }
        return Mono.just(keys)
                .doOnNext(ks -> ks.forEach(key -> {
                    if (null == key) {
                        throw new CacheKeyNullException();
                    }
                }))
                .flatMap(this::doRemoveAll);
    }

    protected abstract Mono<Void> doRemoveAll(Set<? extends K> keys);

    @Override
    public SyncCache<K, V> sync() {
        if (null == syncCache) {
            synchronized (lock) {
                if (null == syncCache) {
                    this.syncCache = new SyncCache.SyncCacheView<>(this);
                }
            }
        }
        return syncCache;
    }

    @Override
    public AsyncCache<K, V> async() {
        if (null == asyncCache) {
            synchronized (lock) {
                if (null == asyncCache) {
                    this.asyncCache = new AsyncCache.AsyncCacheView<>(this);
                }
            }
        }
        return asyncCache;
    }
}
