package com.igeeksky.xcache.config;

import com.igeeksky.xcache.extension.contains.TrueContainsPredicate;
import com.igeeksky.xcache.extension.contains.ContainsPredicate;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.loader.CacheLoader;
import com.igeeksky.xcache.extension.lock.CacheLock;
import com.igeeksky.xcache.extension.lock.LocalCacheLock;
import com.igeeksky.xcache.extension.monitor.CacheMonitor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-09
 */
public class CacheConfig<K, V> {

    private String name;

    private String application;

    private Charset charset;

    private Class<K> keyType;

    private Class<V> valueType;

    private CacheLock cacheLock;

    private KeyConvertor keyConvertor;

    private CacheLoader<K, V> cacheLoader;

    private ContainsPredicate<K> containsPredicate;

    private final List<CacheMonitor<V>> monitors = new ArrayList<>();

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

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
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

    public CacheLock getCacheLock() {
        if (cacheLock != null) {
            return cacheLock;
        }
        return new LocalCacheLock<>();
    }

    public void setCacheLock(CacheLock cacheLock) {
        this.cacheLock = cacheLock;
    }

    public KeyConvertor getKeyConvertor() {
        return keyConvertor;
    }

    public void setKeyConvertor(KeyConvertor keyConvertor) {
        this.keyConvertor = keyConvertor;
    }

    public CacheLoader<K, V> getCacheLoader() {
        return cacheLoader;
    }

    public void setCacheLoader(CacheLoader<K, V> cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    public ContainsPredicate<K> getContainsPredicate() {
        if (containsPredicate != null) {
            return containsPredicate;
        }
        return TrueContainsPredicate.getInstance();
    }

    public void setContainsPredicate(ContainsPredicate<K> containsPredicate) {
        this.containsPredicate = containsPredicate;
    }

    public List<CacheMonitor<V>> getMonitors() {
        return monitors;
    }

    public void addMonitor(CacheMonitor<V> monitor) {
        if (monitor != null) {
            this.monitors.add(monitor);
        }
    }

    public void addMonitors(List<CacheMonitor<V>> monitors) {
        if (monitors != null) {
            this.monitors.addAll(monitors);
        }
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
