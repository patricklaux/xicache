package com.igeeksky.xcache.extension.redis;

import com.igeeksky.xcache.common.ExpiryKeyValue;
import com.igeeksky.xcache.common.KeyValue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public interface RedisConnection extends AutoCloseable {

    String OK = "OK";

    boolean isCluster();

    // String Command --start--
    Mono<byte[]> get(byte[] key);

    Flux<KeyValue<byte[], byte[]>> mget(byte[]... keys);

    Mono<Void> set(byte[] key, byte[] value);

    Mono<Void> psetex(byte[] key, long milliseconds, byte[] value);

    Mono<Void> mset(Map<byte[], byte[]> keyValues);

    Mono<Void> mpsetex(List<ExpiryKeyValue<byte[], byte[]>> keyValues);

    Mono<Long> del(byte[]... keys);
    // String Command --end--

    // Hash Command --start--
    Mono<byte[]> hget(byte[] key, byte[] field);

    Flux<KeyValue<byte[], byte[]>> hmget(byte[] key, byte[]... field);

    Mono<Boolean> hset(byte[] key, byte[] field, byte[] value);

    Mono<Void> hmset(byte[] key, Map<byte[], byte[]> map);

    Mono<Long> hdel(byte[] key, byte[]... fields);
    // Hash Command --end--

}
