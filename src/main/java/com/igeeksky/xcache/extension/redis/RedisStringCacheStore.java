package com.igeeksky.xcache.extension.redis;

import com.igeeksky.xcache.common.*;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.serializer.StringSerializer;
import com.igeeksky.xcache.store.RemoteCacheStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-03
 */
public class RedisStringCacheStore<K, V> implements RemoteCacheStore {

    public static final String STORE_NAME = "redis-string";

    private final boolean useKeyPrefix;

    private final StringSerializer serializer;

    private final RedisStringWriter redisWriter;

    private final CacheKeyPrefix cacheKeyPrefix;

    private final long expireAfterWrite;

    private final long expireAfterWriteMin;

    private final boolean randomAliveTime;

    public RedisStringCacheStore(CacheConfig<K, V> config, StringSerializer serializer, RedisStringWriter redisWriter) {
        this.serializer = serializer;
        this.redisWriter = redisWriter;
        this.useKeyPrefix = config.isUseKeyPrefix();
        this.randomAliveTime = config.isRandomAliveTime();
        this.expireAfterWrite = config.getExpireAfterWrite();
        this.expireAfterWriteMin = (long) (expireAfterWrite * 0.8);
        this.cacheKeyPrefix = new CacheKeyPrefix(config.getName(), config.getCharset(), serializer);
    }

    @Override
    public Mono<CacheValue<byte[]>> get(String key) {
        return Mono.just(key).flatMap(k -> redisWriter.get(toStoreKey(k))).map(CacheValue::new);
    }

    @Override
    public Flux<KeyValue<String, CacheValue<byte[]>>> getAll(Set<? extends String> keys) {
        return Mono.just(keys)
                .map(ks -> {
                    byte[][] keysArray = new byte[ks.size()][];
                    int i = 0;
                    for (String key : ks) {
                        keysArray[i] = toStoreKey(key);
                        i++;
                    }
                    return keysArray;
                })
                .flatMapMany(redisWriter::mget)
                .filter(KeyValue::hasValue)
                .map(kv -> new KeyValue<>(fromStoreKey(kv.getKey()), CacheValues.newCacheValue(kv.getValue())));
    }

    @Override
    public Mono<Void> put(String key, Mono<byte[]> value) {
        return value.flatMap(v -> {
            if (expireAfterWrite <= 0) {
                return redisWriter.set(toStoreKey(key), v);
            } else {
                return redisWriter.psetex(toStoreKey(key), expireAfterWrite, v);
            }
        });
    }

    @Override
    public Mono<Void> putAll(Mono<Map<? extends String, ? extends byte[]>> keyValues) {
        if (expireAfterWrite <= 0) {
            return keyValues.flatMap(kvs -> {
                Map<byte[], byte[]> map = new HashMap<>();
                kvs.forEach((k, v) -> map.put(toStoreKey(k), v));
                return redisWriter.mset(map);
            });
        }
        return keyValues.flatMap(kvs -> {
            List<ExpiryKeyValue<byte[], byte[]>> expiryKeyValues = new ArrayList<>();
            kvs.forEach((k, v) -> expiryKeyValues.add(new ExpiryKeyValue<>(toStoreKey(k), v, timeToLive())));
            return redisWriter.mpsetex(expiryKeyValues);
        });
    }

    @Override
    public Mono<Void> remove(String key) {
        return redisWriter.del(toStoreKey(key)).then();
    }

    @Override
    public Mono<Void> removeAll(Set<? extends String> keys) {
        return Mono.just(keys)
                .map(ks -> {
                    byte[][] keysArray = new byte[ks.size()][];
                    int i = 0;
                    for (String key : ks) {
                        keysArray[i] = toStoreKey(key);
                        i++;
                    }
                    return keysArray;
                })
                .flatMap(redisWriter::del)
                .then();
    }

    @Override
    public Mono<Void> clear() {
        return Mono.error(new UnsupportedOperationException("RedisStringCacheStore doesn't support clear operation"));
    }

    @Override
    public String getStoreName() {
        return STORE_NAME;
    }

    private byte[] toStoreKey(String key) {
        if (useKeyPrefix) {
            return cacheKeyPrefix.concatPrefixBytes(key);
        }
        return serializer.serialize(key);
    }

    private String fromStoreKey(byte[] storeKey) {
        if (useKeyPrefix) {
            return cacheKeyPrefix.removePrefix(storeKey);
        }
        return serializer.deserialize(storeKey);
    }

    /**
     * 随机生成过期时间
     *
     * @return 如果 randomAliveTime 为 true，随机生成过期时间；否则返回配置的 expireAfterWrite
     */
    private long timeToLive() {
        if (randomAliveTime) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return random.nextLong(expireAfterWriteMin, expireAfterWrite);
        }
        return expireAfterWrite;
    }

}