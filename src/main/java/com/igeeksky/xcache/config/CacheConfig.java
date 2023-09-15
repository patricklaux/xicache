package com.igeeksky.xcache.config;

import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.*;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.serializer.Serializer;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-09
 */
public class CacheConfig<K, V> implements Cloneable {

    private String name;
    private Class<K> keyType;
    private Class<V> valueType;
    private CacheLock<K> cacheLock;
    private boolean randomAliveTime = true;
    private boolean enableNullValue = true;
    private boolean enableCompressValue = false;
    private boolean enableSerializeValue = true;
    private Compressor compressor;
    private KeyConvertor keyConvertor;
    private Serializer<V> keySerializer;
    private Serializer<V> valueSerializer;
    private ContainsPredicate<K> containsPredicate;
    private List<CacheMonitor<K, V>> monitors;

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

    public CacheLock<K> getCacheLock() {
        return cacheLock;
    }

    public void setCacheLock(CacheLock<K> cacheLock) {
        this.cacheLock = cacheLock;
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

    public List<CacheMonitor<K, V>> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<CacheMonitor<K, V>> monitors) {
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
