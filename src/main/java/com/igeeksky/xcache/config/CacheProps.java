package com.igeeksky.xcache.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class CacheProps {

    public CacheProps() {
    }

    public CacheProps(String name) {
        this.name = name;
    }

    private String name;

    private String template;

    private String charset;

    private String application;

    private String cacheType;

    private String channel;

    private Config first;

    private Config second;

    private Extension extension;

    private Map<String, Object> metadata;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Config getFirst() {
        return first;
    }

    public void setFirst(Config first) {
        this.first = first;
    }

    public Config getSecond() {
        return second;
    }

    public void setSecond(Config second) {
        this.second = second;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void deepClone(CacheTemplateProps templateProps) {
        if (first == null) {
            first = new Config();
        }
        first.deepClone(templateProps.getFirst());
        if (second == null) {
            second = new Config();
        }
        second.deepClone(templateProps.getSecond());
        if (extension == null) {
            extension = new Extension();
        }
        extension.deepClone(templateProps.getExtension());
        if (metadata == null) {
            metadata = new HashMap<>(templateProps.getMetadata());
        }

        // TODO 完成代码
    }

    public static class Config {

        private String cacheStore;

        private String storeType;

        private Long expireAfterAccess;

        private Long expireAfterWrite;

        private Long maximumSize;

        private String compressor;

        private String keyConvertor;

        private String keySerializer;

        private String valueSerializer;

        private String valueCompressor;

        private Boolean enableKeyPrefix;

        private Boolean enableNullValue;

        private Boolean enableCompressValue;

        private Boolean enableSerializeValue;

        public String getCacheStore() {
            return cacheStore;
        }

        public void setCacheStore(String cacheStore) {
            this.cacheStore = cacheStore;
        }

        public String getStoreType() {
            return storeType;
        }

        public void setStoreType(String storeType) {
            this.storeType = storeType;
        }

        public Long getExpireAfterAccess() {
            return expireAfterAccess;
        }

        public void setExpireAfterAccess(Long expireAfterAccess) {
            this.expireAfterAccess = expireAfterAccess;
        }

        public Long getExpireAfterWrite() {
            return expireAfterWrite;
        }

        public void setExpireAfterWrite(Long expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
        }

        public Long getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(Long maximumSize) {
            this.maximumSize = maximumSize;
        }

        public String getCompressor() {
            return compressor;
        }

        public void setCompressor(String compressor) {
            this.compressor = compressor;
        }

        public String getKeyConvertor() {
            return keyConvertor;
        }

        public void setKeyConvertor(String keyConvertor) {
            this.keyConvertor = keyConvertor;
        }

        public String getKeySerializer() {
            return keySerializer;
        }

        public void setKeySerializer(String keySerializer) {
            this.keySerializer = keySerializer;
        }

        public String getValueSerializer() {
            return valueSerializer;
        }

        public void setValueSerializer(String valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        public String getValueCompressor() {
            return valueCompressor;
        }

        public void setValueCompressor(String valueCompressor) {
            this.valueCompressor = valueCompressor;
        }

        public Boolean getEnableKeyPrefix() {
            return enableKeyPrefix;
        }

        public void setEnableKeyPrefix(Boolean enableKeyPrefix) {
            this.enableKeyPrefix = enableKeyPrefix;
        }

        public Boolean getEnableNullValue() {
            return enableNullValue;
        }

        public void setEnableNullValue(Boolean enableNullValue) {
            this.enableNullValue = enableNullValue;
        }

        public Boolean getEnableCompressValue() {
            return enableCompressValue;
        }

        public void setEnableCompressValue(Boolean enableCompressValue) {
            this.enableCompressValue = enableCompressValue;
        }

        public Boolean getEnableSerializeValue() {
            return enableSerializeValue;
        }

        public void setEnableSerializeValue(Boolean enableSerializeValue) {
            this.enableSerializeValue = enableSerializeValue;
        }

        public void deepClone(CacheTemplateProps.Config config) {
            // TODO 完成代码
        }
    }

    public static class Extension {

        private String cacheLock;

        private Integer cacheLockSize;

        private String containsPredicate;

        private String cacheSync;

        private String cacheStat;

        private String cacheMonitors;

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

        public String getCacheMonitors() {
            return cacheMonitors;
        }

        public void setCacheMonitors(String cacheMonitors) {
            this.cacheMonitors = cacheMonitors;
        }

        public void deepClone(CacheTemplateProps.Extension extension) {
            // TODO 完成代码
        }
    }

}
