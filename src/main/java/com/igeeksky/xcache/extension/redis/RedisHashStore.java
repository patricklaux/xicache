package com.igeeksky.xcache.extension.redis;

import com.igeeksky.xcache.common.CacheKeyPrefix;
import com.igeeksky.xcache.common.CacheValue;
import com.igeeksky.xcache.common.CacheValues;
import com.igeeksky.xcache.common.KeyValue;
import com.igeeksky.xcache.config.CacheConfig;
import com.igeeksky.xcache.extension.serializer.StringSerializer;
import com.igeeksky.xcache.store.RemoteStore;
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
public class RedisHashStore<K, V> implements RemoteStore {

    public static final String STORE_NAME = "redis-hash";

    private final byte[] name;

    private final byte[][] redisHashKeys;

    private final StringSerializer serializer;

    private final RedisConnection connection;

    private final CacheKeyPrefix cacheKeyPrefix;

    public RedisHashStore(CacheConfig<K, V> config, StringSerializer serializer, RedisConnection connection) {
        this.serializer = serializer;
        this.connection = connection;
        this.name = serializer.serialize(config.getName());
        this.cacheKeyPrefix = new CacheKeyPrefix(config.getName(), serializer);
        this.redisHashKeys = (connection.isCluster()) ? initHashKeys() : null;
    }

    /**
     * <p>初始化哈希表名</p>
     * <p/>
     * 仅集群模式时使用，可以将键和值分散到不同的节点。
     *
     * @return 16384个哈希表名（cache-name::1, cache-name::2……）
     */
    private byte[][] initHashKeys() {
        // TODO 数值可配置
        int len = 16384;
        byte[][] keys = new byte[len][];
        for (int i = 0; i < len; i++) {
            keys[i] = cacheKeyPrefix.createHashKey(i);
        }
        return keys;
    }

    @Override
    public Mono<CacheValue<byte[]>> get(String key) {
        return Mono.just(key)
                .map(this::toStoreKey)
                .flatMap(k -> {
                    if (connection.isCluster()) {
                        return connection.hget(selectHashKey(k), toStoreKey(key));
                    }
                    return connection.hget(name, toStoreKey(key));
                })
                .map(CacheValues::newCacheValue);
    }

    @Override
    public Flux<KeyValue<String, CacheValue<byte[]>>> getAll(Set<? extends String> keys) {
        if (connection.isCluster()) {
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
                        return connection.hmget(hashKey, fields.toArray(new byte[size][]));
                    })
                    .filter(KeyValue::hasValue)
                    .map(kv -> new KeyValue<>(fromStoreKey(kv.getKey()), CacheValues.newCacheValue(kv.getValue())));
        }

        return Mono.just(keys)
                .flatMapMany(ks -> {
                    List<byte[]> fields = new ArrayList<>(ks.size());
                    ks.forEach(k -> fields.add(toStoreKey(k)));
                    int size = fields.size();
                    return connection.hmget(name, fields.toArray(new byte[size][]));
                })
                .filter(KeyValue::hasValue)
                .map(kv -> new KeyValue<>(fromStoreKey(kv.getKey()), CacheValues.newCacheValue(kv.getValue())));
    }

    @Override
    public Mono<Void> put(String key, Mono<byte[]> value) {
        return value.flatMap(v -> {
                    var field = toStoreKey(key);
                    if (connection.isCluster()) {
                        return connection.hset(selectHashKey(field), field, v);
                    }
                    return connection.hset(name, field, v);
                })
                .then();
    }

    @Override
    public Mono<Void> putAll(Mono<Map<? extends String, ? extends byte[]>> keyValues) {
        if (connection.isCluster()) {
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
                    .flatMap(entry -> connection.hmset(entry.getKey(), entry.getValue()))
                    .then();
        }

        return keyValues.flatMap(kvs -> {
            Map<byte[], byte[]> map = new HashMap<>(kvs.size());
            kvs.forEach((k, v) -> map.put(toStoreKey(k), v));
            return connection.hmset(name, map);
        });
    }

    @Override
    public Mono<Void> remove(String key) {
        return Mono.just(key)
                .map(this::toStoreKey)
                .flatMap(field -> {
                    if (connection.isCluster()) {
                        return connection.hdel(selectHashKey(field), field);
                    }
                    return connection.hdel(name, field);
                })
                .then();
    }

    @Override
    public Mono<Void> removeAll(Set<? extends String> keys) {
        if (connection.isCluster()) {
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
                        return connection.hdel(hashKey, fields.toArray(new byte[size][]));
                    })
                    .then();
        }

        return Mono.just(keys)
                .flatMap(ks -> {
                    List<byte[]> fields = new ArrayList<>(ks.size());
                    ks.forEach(k -> fields.add(toStoreKey(k)));
                    int size = fields.size();
                    return connection.hdel(name, fields.toArray(new byte[size][])).then();
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