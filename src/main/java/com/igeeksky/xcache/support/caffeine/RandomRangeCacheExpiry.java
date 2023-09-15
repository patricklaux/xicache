package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Expiry;
import com.igeeksky.xcache.common.CacheValue;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p><b>存活时间随机策略</b></p>
 * <p>创建（更新）后的最大存活时间：expireAfterCreate 的设定值</p>
 * <p>创建（更新）后的最小存活时间：expireAfterCreate * 0.8</p>
 * <p>访问后的存活时间：expiresAfterAccess 与 当前剩余存活时间 两者的大值，</p>
 * 即 Math.max(expiresAfterAccess, currentDuration)
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-16
 */
public class RandomRangeCacheExpiry<K, V> implements Expiry<K, CacheValue<V>> {

    // 创建（更新）后的最小存活时间
    private final long originExpireAfterCreateNanos;

    // 创建（更新）后的最大存活时间
    private final long boundExpireAfterCreateNanos;

    // 访问后的存活时间
    private final long expiresAfterAccessNanos;

    public RandomRangeCacheExpiry(@NonNull Duration expireAfterCreate, @NonNull Duration expiresAfterAccess) {
        this.boundExpireAfterCreateNanos = expireAfterCreate.toNanos();
        this.originExpireAfterCreateNanos = (long) (boundExpireAfterCreateNanos * 0.8);
        this.expiresAfterAccessNanos = expiresAfterAccess.toNanos();
    }

    @Override
    public long expireAfterCreate(@NonNull K key, @NonNull CacheValue<V> cacheValue, long currentTime) {
        return ThreadLocalRandom.current().nextLong(originExpireAfterCreateNanos, boundExpireAfterCreateNanos);
    }

    @Override
    public long expireAfterUpdate(@NonNull K key, @NonNull CacheValue<V> cacheValue, long currentTime, @NonNegative long currentDuration) {
        return expireAfterCreate(key, cacheValue, currentTime);
    }

    @Override
    public long expireAfterRead(@NonNull K key, @NonNull CacheValue<V> cacheValue, long currentTime, @NonNegative long currentDuration) {
        return Math.max(currentDuration, expiresAfterAccessNanos);
    }
}