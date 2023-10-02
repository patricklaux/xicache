package com.igeeksky.xcache.config.props;

import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class ExtensionProps implements Cloneable {

    private String keyConvertor;
    private String cacheLock;
    private Integer cacheLockSize;
    private String containsPredicate;
    private String cacheSync;
    private String cacheSyncChannel;
    private String cacheSyncSerializer;
    private String cacheStat;
    private String cacheLoader;
    private String cacheMonitors;

    public String getKeyConvertor() {
        return keyConvertor;
    }

    public void setKeyConvertor(String keyConvertor) {
        this.keyConvertor = keyConvertor;
    }

    public String getCacheLock() {
        return cacheLock;
    }

    public void setCacheLock(String cacheLock) {
        this.cacheLock = cacheLock;
    }

    public Integer getCacheLockSize() {
        return cacheLockSize;
    }

    public void setCacheLockSize(Integer cacheLockSize) {
        this.cacheLockSize = cacheLockSize;
    }

    public String getContainsPredicate() {
        return containsPredicate;
    }

    public void setContainsPredicate(String containsPredicate) {
        this.containsPredicate = containsPredicate;
    }

    public String getCacheSync() {
        return cacheSync;
    }

    public void setCacheSync(String cacheSync) {
        this.cacheSync = cacheSync;
    }

    public String getCacheSyncChannel() {
        return cacheSyncChannel;
    }

    public void setCacheSyncChannel(String cacheSyncChannel) {
        this.cacheSyncChannel = cacheSyncChannel;
    }

    public String getCacheSyncSerializer() {
        return cacheSyncSerializer;
    }

    public void setCacheSyncSerializer(String cacheSyncSerializer) {
        this.cacheSyncSerializer = cacheSyncSerializer;
    }

    public String getCacheStat() {
        return cacheStat;
    }

    public void setCacheStat(String cacheStat) {
        this.cacheStat = cacheStat;
    }

    public String getCacheLoader() {
        return cacheLoader;
    }

    public void setCacheLoader(String cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    public String getCacheMonitors() {
        return cacheMonitors;
    }

    public void setCacheMonitors(String cacheMonitors) {
        this.cacheMonitors = cacheMonitors;
    }

    @Override
    public ExtensionProps clone() throws CloneNotSupportedException {
        return (ExtensionProps) super.clone();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("keyConvertor='" + keyConvertor + "'")
                .add("cacheLock='" + cacheLock + "'")
                .add("cacheLockSize=" + cacheLockSize)
                .add("containsPredicate='" + containsPredicate + "'")
                .add("cacheSync='" + cacheSync + "'")
                .add("cacheSyncChannel='" + cacheSyncChannel + "'")
                .add("cacheSyncSerializer='" + cacheSyncSerializer + "'")
                .add("cacheStat='" + cacheStat + "'")
                .add("cacheLoader='" + cacheLoader + "'")
                .add("cacheMonitors='" + cacheMonitors + "'")
                .toString();
    }
}
