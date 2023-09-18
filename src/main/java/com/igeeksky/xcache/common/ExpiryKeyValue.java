package com.igeeksky.xcache.common;

/**
 *
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-18
 */
public class ExpiryKeyValue<K, V> extends KeyValue<K, V> {

    private final long ttl;

    public ExpiryKeyValue(K key, V value, long ttl) {
        super(key, value);
        this.ttl = ttl;
    }

    /**
     * @return time to live, type: milliseconds
     */
    public long getTtl() {

        return ttl;
    }

}
