package com.igeeksky.xcache.config;

import com.igeeksky.xcache.extension.Compressor;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.serializer.Serializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-09
 */
public class CacheConfig<K, V> implements Cloneable {

    // both
    private String name;

    // Remote
    private String storeName;

    // both
    private Charset charset = StandardCharsets.UTF_8;

    // both
    private Class<K> keyType;

    // both
    private Class<V> valueType;

    // Local & Remote
    private long expireAfterWrite;

    // Remote String
    private boolean useKeyPrefix = true;

    // Local & Remote
    private boolean randomAliveTime = true;

    // Local & Remote
    private boolean enableNullValue = true;

    // Local & Remote
    private boolean enableCompressValue = false;

    // Local & Remote
    private boolean enableSerializeValue = true;

    // Local & Remote
    private Compressor compressor;

    // Local & Remote
    private CacheLock<K> cacheLock;

    // both
    private KeyConvertor keyConvertor;

    // Remote
    private Serializer<V> keySerializer;

    // Local & Remote
    private Serializer<V> valueSerializer;

    // both
    private ContainsPredicate<K> containsPredicate;

    // both
    private List<CacheMonitor<V>> monitors;

    public CacheConfig() {
    }

    public CacheConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Class<K> getKeyType() {
        return keyType;
    }

    public void setKeyType(Class<K> keyType) {
        this.keyType = keyType;
    }

    public Class<V> getValueType() {
        return valueType;
    }

    public void setValueType(Class<V> valueType) {
        this.valueType = valueType;
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public boolean isUseKeyPrefix() {
        return useKeyPrefix;
    }

    public void setUseKeyPrefix(boolean useKeyPrefix) {
        this.useKeyPrefix = useKeyPrefix;
    }

    public boolean isRandomAliveTime() {
        return randomAliveTime;
    }

    public void setRandomAliveTime(boolean randomAliveTime) {
        this.randomAliveTime = randomAliveTime;
    }

    public boolean isEnableNullValue() {
        return enableNullValue;
    }

    public void setEnableNullValue(boolean enableNullValue) {
        this.enableNullValue = enableNullValue;
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

    public Compressor getCompressor() {
        return compressor;
    }

    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    public CacheLock<K> getCacheLock() {
        return cacheLock;
    }

    public void setCacheLock(CacheLock<K> cacheLock) {
        this.cacheLock = cacheLock;
    }

    public KeyConvertor getKeyConvertor() {
        return keyConvertor;
    }

    public void setKeyConvertor(KeyConvertor keyConvertor) {
        this.keyConvertor = keyConvertor;
    }

    public Serializer<V> getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Serializer<V> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(Serializer<V> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public ContainsPredicate<K> getContainsPredicate() {
        return containsPredicate;
    }

    public void setContainsPredicate(ContainsPredicate<K> containsPredicate) {
        this.containsPredicate = containsPredicate;
    }

    public List<CacheMonitor<V>> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<CacheMonitor<V>> monitors) {
        this.monitors = monitors;
    }

    /**
     * 浅 copy
     *
     * @return CacheConfig<K, V>
     */
    @SuppressWarnings("unchecked")
    public CacheConfig<K, V> clone() {
        try {
            return (CacheConfig<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
