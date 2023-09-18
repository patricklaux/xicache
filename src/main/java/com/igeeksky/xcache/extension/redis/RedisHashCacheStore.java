package com.igeeksky.xcache.extension.redis;

import com.igeeksky.xcache.common.CacheKeyPrefix;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.CacheValues;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.serializer.StringSerializer;
import com.igeeksky.xcache.store.RemoteCacheStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * <p><b>使用哈希表作为缓存</b></p>
 * <p>
 * 采用哈希表作为缓存，因为 Redis的限制，无法设置过期时间。
 *
 * <p> 哈希表名设计：</p>
 * <p> 1. 单点模式、主从模式、哨兵模式：使用缓存名称，即配置的 cache-name</p>
 * <p> 2. 集群模式：默认生成 16384 个哈希表，哈希表名格式为（cache-name::1, cache-name::2……）</p>
 * 这 16384 个哈希表将分散到集群的不同节点，设计目的是为了避免大量访问同一节点。
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-03
 */
public class RedisHashCacheStore<K, V> implements RemoteCacheStore {

    public static final String STORE_NAME = "redis-hash";

    private final byte[] name;

    private final byte[][] redisHashKeys;

    private final StringSerializer serializer;

    private final RedisHashWriter redisHashWriter;

    private final CacheKeyPrefix cacheKeyPrefix;

    public RedisHashCacheStore(CacheConfig<K, V> config, StringSerializer serializer, RedisHashWriter redisHashWriter) {
        this.serializer = serializer;
        this.redisHashWriter = redisHashWriter;
        this.name = serializer.serialize(config.getName());
        this.cacheKeyPrefix = new CacheKeyPrefix(config.getName(), config.getCharset(), serializer);
        if (redisHashWriter.isCluster()) {
            this.redisHashKeys = initHashKeys();
        } else {
            this.redisHashKeys = null;
        }
    }

    /**
     * <p>初始化哈希表名</p>
     * <p/>
     * 仅集群模式时使用，可以将键和值分散到不同的节点。
     *
     * @return 16384个哈希表名（cache-name::1, cache-name::2……）
     */
    private byte[][] initHashKeys() {
        int length = 16384;
        byte[][] keys = new byte[length][];
        for (int i = 0; i < length; i++) {
            keys[i] = cacheKeyPrefix.createHashKey(i);
        }
        return keys;
    }

    @Override
    public Mono<CacheValue<byte[]>> get(String key) {
        return Mono.just(key)
                .map(this::toStoreKey)
                .flatMap(k -> {
                    if (redisHashWriter.isCluster()) {
                        return redisHashWriter.hget(selectHashKey(k), toStoreKey(key));
                    }
                    return redisHashWriter.hget(name, toStoreKey(key));
                })
                .map(CacheValues::newCacheValue);
    }

    @Override
    public Flux<KeyValue<String, CacheValue<byte[]>>> getAll(Set<? extends String> keys) {
        if (redisHashWriter.isCluster()) {
            return Mono.just(keys)
                    .map(ks -> {
                        Map<byte[], List<byte[]>> keyListMap = new HashMap<>();
                        for (String key : ks) {
                            byte[] field = toStoreKey(key);
                            byte[] hashKey = selectHashKey(field);
                            List<byte[]> list = keyListMap.computeIfAbsent(hashKey, hk -> new ArrayList<>());
                            list.add(field);
                        }
                        return keyListMap.entrySet();
                    })
                    .flatMapMany(Flux::fromIterable)
                    .flatMap(entry -> {
                        byte[] hashKey = entry.getKey();
                        List<byte[]> fields = entry.getValue();
                        int size = fields.size();
                        return redisHashWriter.hmget(hashKey, fields.toArray(new byte[size][]));
                    })
                    .filter(KeyValue::hasValue)
                    .map(kv -> new KeyValue<>(fromStoreKey(kv.getKey()), CacheValues.newCacheValue(kv.getValue())));
        }

        return Mono.just(keys)
                .flatMapMany(ks -> {
                    List<byte[]> fields = new ArrayList<>(ks.size());
                    ks.forEach(k -> fields.add(toStoreKey(k)));
                    int size = fields.size();
                    return redisHashWriter.hmget(name, fields.toArray(new byte[size][]));
                })
                .filter(KeyValue::hasValue)
                .map(kv -> new KeyValue<>(fromStoreKey(kv.getKey()), CacheValues.newCacheValue(kv.getValue())));
    }

    @Override
    public Mono<Void> put(String key, Mono<byte[]> value) {
        return value.flatMap(v -> {
                    if (redisHashWriter.isCluster()) {
                        return redisHashWriter.hset(selectHashKey(v), toStoreKey(key), v);
                    }
                    return redisHashWriter.hset(name, toStoreKey(key), v);
                })
                .then();
    }

    @Override
    public Mono<Void> putAll(Mono<Map<? extends String, ? extends byte[]>> keyValues) {
        if (redisHashWriter.isCluster()) {
            keyValues.map(kvs -> {
                        Map<byte[], Map<byte[], byte[]>> hashKeyMap = new HashMap<>(kvs.size());
                        for (Map.Entry<? extends String, ? extends byte[]> entry : kvs.entrySet()) {
                            // 根据 field 分配到不同的哈希表
                            byte[] field = toStoreKey(entry.getKey());
                            byte[] hashKey = selectHashKey(field);
                            byte[] value = entry.getValue();
                            Map<byte[], byte[]> splitMap = hashKeyMap.computeIfAbsent(hashKey, k -> new HashMap<>());
                            splitMap.put(field, value);
                        }
                        return hashKeyMap.entrySet();
                    })
                    .flatMapMany(Flux::fromIterable)
                    .flatMap(entry -> redisHashWriter.hmset(entry.getKey(), entry.getValue()))
                    .then();
        }

        return keyValues.flatMap(kvs -> {
            Map<byte[], byte[]> map = new HashMap<>(kvs.size());
            kvs.forEach((k, v) -> map.put(toStoreKey(k), v));
            return redisHashWriter.hmset(name, map);
        });
    }

    @Override
    public Mono<Void> remove(String key) {
        return Mono.just(key)
                .map(this::toStoreKey)
                .flatMap(field -> {
                    if (redisHashWriter.isCluster()) {
                        return redisHashWriter.hdel(selectHashKey(field), field);
                    }
                    return redisHashWriter.hdel(name, field);
                })
                .then();
    }

    @Override
    public Mono<Void> removeAll(Set<? extends String> keys) {
        if (redisHashWriter.isCluster()) {
            return Mono.just(keys)
                    .map(ks -> {
                        Map<byte[], List<byte[]>> keyListMap = new HashMap<>();
                        for (String key : ks) {
                            byte[] field = toStoreKey(key);
                            byte[] hashKey = selectHashKey(field);
                            List<byte[]> list = keyListMap.computeIfAbsent(hashKey, hk -> new ArrayList<>());
                            list.add(field);
                        }
                        return keyListMap.entrySet();
                    })
                    .flatMapMany(Flux::fromIterable)
                    .flatMap(entry -> {
                        byte[] hashKey = entry.getKey();
                        List<byte[]> fields = entry.getValue();
                        int size = fields.size();
                        return redisHashWriter.hdel(hashKey, fields.toArray(new byte[size][]));
                    })
                    .then();
        }

        return Mono.just(keys)
                .flatMap(ks -> {
                    List<byte[]> fields = new ArrayList<>(ks.size());
                    ks.forEach(k -> fields.add(toStoreKey(k)));
                    int size = fields.size();
                    return redisHashWriter.hdel(name, fields.toArray(new byte[size][])).then();
                })
                .then();
    }

    @Override
    public Mono<Void> clear() {
        return Mono.error(new UnsupportedOperationException("RedisHashCacheStore doesn't support clear operation"));
    }

    @Override
    public String getStoreName() {
        return STORE_NAME;
    }

    private String fromStoreKey(byte[] field) {
        return serializer.deserialize(field);
    }

    private byte[] toStoreKey(String field) {
        return serializer.serialize(field);
    }

    /**
     * 根据 field 获取哈希表名称
     *
     * @param field 键
     * @return 哈希表名
     */
    private byte[] selectHashKey(byte[] field) {
        return redisHashKeys[CRC16.crc16(field)];
    }

}