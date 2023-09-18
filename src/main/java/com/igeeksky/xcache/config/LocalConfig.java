package com.igeeksky.xcache.config;

import com.igeeksky.xcache.extension.Compressor;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.serializer.Serializer;

/**
 * 本地缓存配置
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
public class LocalConfig<K, V> {

    // Local & Remote
    private String storeName;

    // Local
    private int initialSize;

    // Local
    private long maximumSize;

    // Local(Caffeine)
    private long maximumWeight;

    // Local(Caffeine)
    private String keyStrength;

    // Local(Caffeine)
    private String valueStrength;

    // Local & Remote
    private long expireAfterWrite;

    // Local
    private long expireAfterAccess;

    // Local & Remote
    private boolean enableRandomTtl = true;

    // Local & Remote
    private boolean enableNullValue = true;

    // Local & Remote
    private boolean enableCompressValue = false;

    // Local & Remote
    private boolean enableSerializeValue = false;

    // Local & Remote
    private Compressor valueCompressor;

    // Local & Remote
    private Serializer<V> valueSerializer;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public long getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
    }

    public long getMaximumWeight() {
        return maximumWeight;
    }

    public void setMaximumWeight(long maximumWeight) {
        this.maximumWeight = maximumWeight;
    }

    public String getKeyStrength() {
        return keyStrength;
    }

    public void setKeyStrength(String keyStrength) {
        this.keyStrength = keyStrength;
    }

    public String getValueStrength() {
        return valueStrength;
    }

    public void setValueStrength(String valueStrength) {
        this.valueStrength = valueStrength;
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
    }

    public boolean isEnableNullValue() {
        return enableNullValue;
    }

    public void setEnableNullValue(boolean enableNullValue) {
        this.enableNullValue = enableNullValue;
    }

    public boolean isEnableRandomTtl() {
        return enableRandomTtl;
    }

    public void setEnableRandomTtl(boolean enableRandomTtl) {
        this.enableRandomTtl = enableRandomTtl;
    }

    public boolean isEnableCompressValue() {
        return enableCompressValue;
    }

    public void setEnableCompressValue(boolean enableCompressValue) {
        this.enableCompressValue = enableCompressValue;
    }

    public boolean isEnableSerializeValue() {
        return enableSerializeValue;
    }

    public void setEnableSerializeValue(boolean enableSerializeValue) {
        this.enableSerializeValue = enableSerializeValue;
    }

    public Compressor getValueCompressor() {
        return valueCompressor;
    }

    public void setValueCompressor(Compressor valueCompressor) {
        this.valueCompressor = valueCompressor;
    }

    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(Serializer<V> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

}
