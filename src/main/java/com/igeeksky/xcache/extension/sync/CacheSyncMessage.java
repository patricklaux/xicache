package com.igeeksky.xcache.extension.sync;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-11
 */
public class CacheSyncMessage implements Serializable {

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

    private Set<String> keys;

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

    public CacheSyncMessage setType(int type) {
        this.type = type;
        return this;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public CacheSyncMessage setKeys(Set<String> keys) {
        this.keys = keys;
        return this;
    }

    public CacheSyncMessage addKey(String key) {
        if (this.keys == null) {
            this.keys = new LinkedHashSet<>();
        }
        this.keys.add(key);
        return this;
    }

}
