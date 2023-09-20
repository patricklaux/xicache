package com.igeeksky.xcache.config.props;

import com.igeeksky.xcache.config.CacheConstants;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class ExtensionProps {

    private String cacheLock;
    private Integer cacheLockSize;
    private String containsPredicate;
    private String cacheSync;
    private String cacheStat;
    private String cacheStatChannel;
    private String cacheMonitors;

    public static ExtensionProps createDefault() {
        ExtensionProps extensionProps = new ExtensionProps();
        extensionProps.setCacheLock(CacheConstants.EXTENSION_CACHE_LOCK);
        // TODO 完善扩展配置默认值
        return extensionProps;
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

    public String getCacheStat() {
        return cacheStat;
    }

    public void setCacheStat(String cacheStat) {
        this.cacheStat = cacheStat;
    }

    public String getCacheStatChannel() {
        return cacheStatChannel;
    }

    public void setCacheStatChannel(String cacheStatChannel) {
        this.cacheStatChannel = cacheStatChannel;
    }

    public String getCacheMonitors() {
        return cacheMonitors;
    }

    public void setCacheMonitors(String cacheMonitors) {
        this.cacheMonitors = cacheMonitors;
    }

    public ExtensionProps deepClone() {
        ExtensionProps clone = new ExtensionProps();
        clone.setCacheLock(this.getCacheLock());
        // TODO 深度克隆 ExtensionProps
        return clone;
    }
}
