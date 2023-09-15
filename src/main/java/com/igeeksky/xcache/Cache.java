package com.igeeksky.xcache;

import com.igeeksky.xcache.common.CacheLoader;
import com.igeeksky.xcache.common.CacheValue;
import reactor.core.publisher.Mono;

/**
 * <p>缓存</p>
 *
 * @author Patrick.Lau
 * @since 0.0.4 2021-09-05
 */
public interface Cache<K, V> extends ReactiveCache<K, V> {

    // TODO 压缩(解压缩)，统计，广播，键序列化(反序列化)，键转字符串，值序列化(反序列化)，是否允许为空

    String getName();

    Class<K> getKeyType();

    Class<V> getValueType();

    Mono<CacheValue<V>> get(K key, CacheLoader<K, V> cacheLoader);

    SyncCache<K, V> sync();

    AsyncCache<K, V> async();

}