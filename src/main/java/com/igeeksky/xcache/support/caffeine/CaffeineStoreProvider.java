package com.igeeksky.xcache.support.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.Weigher;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.config.CacheConfigException;
import com.igeeksky.xcache.config.CacheConstants;
import com.igeeksky.xcache.store.LocalStore;
import com.igeeksky.xcache.store.LocalStoreProvider;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * Caffeine 本地缓存提供者
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class CaffeineStoreProvider implements LocalStoreProvider {

    private final CaffeineExpiryProvider expiryProvider;
    private final CaffeineWeigherProvider weigherProvider;

    public CaffeineStoreProvider(CaffeineExpiryProvider expiryProvider, CaffeineWeigherProvider weigherProvider) {
        this.expiryProvider = expiryProvider;
        this.weigherProvider = weigherProvider;
    }

    @Override
    public <K, V> LocalStore getLocalStore(CacheConfig<K, V> config) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        // 1. 设置基于时间的驱逐策略
        // 1.1. 基于时间的自定义驱逐策略
        if (expiryProvider != null) {
            Expiry<String, CacheValue<Object>> expiry = expiryProvider.get(config.getName());
            if (expiry != null) {
                builder.expireAfter(expiry);
                return createCaffeineStore(config, builder);
            }
        }

        boolean enableRandomTtl = config.getLocalConfig().isEnableRandomTtl();
        long expireAfterWrite = config.getLocalConfig().getExpireAfterWrite();
        long expireAfterAccess = config.getLocalConfig().getExpireAfterAccess();

        // 1.2. 基于随机时间的驱逐策略
        if (enableRandomTtl) {
            if (expireAfterWrite <= 0L) {
                throw new CacheConfigException("enableRandomTtl: expireAfterWrite must be greater than 0");
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

    private <K, V> LocalStore createCaffeineStore(CacheConfig<K, V> config, Caffeine<Object, Object> builder) {
        // 2. 设置初始化缓存容量
        int initialCapacity = config.getLocalConfig().getInitialCapacity();
        if (initialCapacity > 0) {
            builder.initialCapacity(initialCapacity);
        }

        // 3. 基于容量的驱逐策略
        long maximumSize = config.getLocalConfig().getMaximumSize();
        if (maximumSize > 0) {
            builder.maximumSize(maximumSize);
        }

        // 4. 基于权重的驱逐策略
        long maximumWeight = config.getLocalConfig().getMaximumWeight();
        if (maximumWeight > 0) {
            builder.maximumWeight(maximumWeight);
            if (weigherProvider != null) {
                Weigher<String, CacheValue<Object>> weigher = weigherProvider.get(config.getName());
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
        String keyStrength = StringUtils.toLowerCase(config.getLocalConfig().getKeyStrength());
        if (StringUtils.hasLength(keyStrength) && !Objects.equals(CacheConstants.NONE, StringUtils.toUpperCase(keyStrength))) {
            if (Objects.equals(weak, keyStrength)) {
                builder.weakKeys();
            } else {
                throw new CacheConfigException("keyStrength:" + keyStrength + "] can only be set to 'weak'");
            }
        }

        // 5.2. 基于 value 的弱引用和软引用驱逐策略
        String valueStrength = StringUtils.toLowerCase(config.getLocalConfig().getValueStrength());
        if (StringUtils.hasLength(valueStrength) && !Objects.equals(CacheConstants.NONE, StringUtils.toUpperCase(valueStrength))) {
            if (Objects.equals(weak, valueStrength)) {
                builder.weakValues();
            } else if (Objects.equals(soft, valueStrength)) {
                builder.softValues();
            } else {
                throw new CacheConfigException("valueStrength:[" + valueStrength + "] can only be set to 'weak' or 'soft'");
            }
        }

        return new CaffeineStore<>(builder.build());
    }

}
