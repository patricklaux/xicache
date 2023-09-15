package com.igeeksky.xcache.extension.sync;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-11
 */
public class CacheSyncMessage<K> implements Serializable {

    private static final long serialVersionUID = -9034710794861743946L;

    public static final int TYPE_REMOVE = 1;
    public static final int TYPE_CLEAR = 2;

    /**
     * service id
     */
    private String sid;

    /**
     * operation type
     */
    private int type;

    private Set<K> keys;

    public CacheSyncMessage() {
    }

    public CacheSyncMessage(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getType() {
        return type;
    }

    public CacheSyncMessage<K> setType(int type) {
        this.type = type;
        return this;
    }

    public Set<K> getKeys() {
        return keys;
    }

    public CacheSyncMessage<K> setKeys(Set<K> keys) {
        this.keys = keys;
        return this;
    }

    public CacheSyncMessage<K> addKey(K key) {
        if (this.keys == null) {
            this.keys = new LinkedHashSet<>();
        }
        this.keys.add(key);
        return this;
    }

}
