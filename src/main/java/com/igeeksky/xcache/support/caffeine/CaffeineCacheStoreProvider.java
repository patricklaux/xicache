package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.Weigher;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.config.CacheConfigException;
import com.igeeksky.xcache.store.LocalCacheStore;
import com.igeeksky.xcache.store.LocalCacheStoreProvider;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * Caffeine 本地缓存提供者
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class CaffeineCacheStoreProvider implements LocalCacheStoreProvider {

    private final CaffeineExpiryProvider expiryProvider;
    private final CaffeineWeigherProvider weigherProvider;

    public CaffeineCacheStoreProvider(CaffeineExpiryProvider expiryProvider, CaffeineWeigherProvider weigherProvider) {
        this.expiryProvider = expiryProvider;
        this.weigherProvider = weigherProvider;
    }

    @Override
    public <K, V> LocalCacheStore getLocalCacheStore(CacheConfig<K, V> config) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        // 1. 设置基于时间的驱逐策略
        // 1.1. 基于时间的自定义驱逐策略
        if (expiryProvider != null) {
            Expiry<String, CacheValue<Object>> expiry = expiryProvider.get(config);
            if (null != expiry) {
                builder.expireAfter(expiry);
                return createCaffeineStore(config, builder);
            }
        }

        boolean randomAliveTime = config.isRandomAliveTime();
        long expireAfterWrite = config.getExpireAfterWrite();
        long expireAfterAccess = config.getExpireAfterAccess();

        // 1.2. 基于随机时间的驱逐策略
        if (randomAliveTime) {
            if (expireAfterWrite <= 0) {
                throw new CacheConfigException("randomAliveTime: expireAfterWrite must be greater than 0");
            }
            Duration durationWrite = Duration.ofMillis(expireAfterWrite);
            Duration durationAccess = Duration.ofMillis(expireAfterAccess);
            RandomRangeExpiry<String, Object> expiry = new RandomRangeExpiry<>(durationWrite, durationAccess);
            builder.expireAfter(expiry);
            return createCaffeineStore(config, builder);
        }

        // 1.3. 基于固定时间的驱逐策略
        if (expireAfterWrite > 0) {
            builder.expireAfterWrite(Duration.ofMillis(expireAfterWrite));
        }
        if (expireAfterAccess > 0) {
            builder.expireAfterAccess(Duration.ofMillis(expireAfterAccess));
        }

        return createCaffeineStore(config, builder);
    }

    private <K, V> LocalCacheStore createCaffeineStore(CacheConfig<K, V> config, Caffeine<Object, Object> builder) {
        // 2. 设置初始化缓存容量
        int initialCapacity = config.getInitialCapacity();
        if (initialCapacity > 0) {
            builder.initialCapacity(initialCapacity);
        }

        // 3. 基于容量的驱逐策略
        long maximumSize = config.getMaximumSize();
        if (maximumSize > 0) {
            builder.maximumSize(maximumSize);
        }

        // 4. 基于权重的驱逐策略
        long maximumWeight = config.getMaximumWeight();
        if (maximumWeight > 0) {
            builder.maximumWeight(maximumWeight);
            if (weigherProvider != null) {
                Weigher<String, CacheValue<Object>> weigher = weigherProvider.get(config);
                if (null == weigher) {
                    throw new CacheConfigException("Cache:[" + config.getName() + "]. The weigher is not predefined");
                }
                builder.weigher(weigher);
            } else {
                builder.weigher(Weigher.singletonWeigher());
            }
        }

        // 5. 基于引用的驱逐策略
        String weak = "weak";
        String soft = "soft";

        // 5.1. 基于 Key 的弱引用驱逐策略
        String keyStrength = StringUtils.toLowerCase(config.getKeyStrength());
        if (StringUtils.hasLength(keyStrength)) {
            if (Objects.equals(weak, keyStrength)) {
                builder.weakKeys();
            } else {
                throw new CacheConfigException("keyStrength:" + keyStrength + "] can only be set to 'weak'");
            }
        }

        // 5.2. 基于 value 的弱引用和软引用驱逐策略
        String valueStrength = StringUtils.toLowerCase(config.getValueStrength());
        if (StringUtils.hasLength(valueStrength)) {
            if (Objects.equals(weak, valueStrength)) {
                builder.weakValues();
            } else if (Objects.equals(soft, valueStrength)) {
                builder.softValues();
            } else {
                throw new CacheConfigException("valueStrength:[" + valueStrength + "] can only be set to 'weak' or 'soft'");
            }
        }

        return new CaffeineCacheStore<>(builder.build());
    }

}
