package com.igeeksky.xcache.config;

import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-09
 */
public class CacheConfig<K, V> {

    // both
    private String name;

    // both
    private Charset charset = StandardCharsets.UTF_8;

    // both
    private Class<K> keyType;

    // both
    private Class<V> valueType;

    // both
    private CacheLock<K> cacheLock;

    // both
    private KeyConvertor keyConvertor;

    // both
    private ContainsPredicate<K> containsPredicate;

    // both
    private List<CacheMonitor<V>> monitors = new ArrayList<>();

    private LocalConfig<K, V> localConfig = new LocalConfig<>();

    private RemoteConfig<K, V> remoteConfig = new RemoteConfig<>();

    private Map<String, Object> metadata = new HashMap<>();

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

    public LocalConfig<K, V> getLocalConfig() {
        return localConfig;
    }

    public void setLocalConfig(LocalConfig<K, V> localConfig) {
        this.localConfig = localConfig;
    }

    public RemoteConfig<K, V> getRemoteConfig() {
        return remoteConfig;
    }

    public void setRemoteConfig(RemoteConfig<K, V> remoteConfig) {
        this.remoteConfig = remoteConfig;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

}
