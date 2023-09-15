package com.igeeksky.xcache.config;

import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class CacheTemplateProps {

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

    public static class Config {

        private String cacheStore;

        private String storeType;

        private long expireAfterAccess;

        private long expireAfterWrite;

        private long maximumSize;

        private String compressor;

        private String keyConvertor;

        private String keySerializer;

        private String valueSerializer;

        private String valueCompressor;

        private boolean enableKeyPrefix = true;

        private boolean enableNullValue = true;

        private boolean enableCompressValue = false;

        private boolean enableSerializeValue = true;

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

        public long getExpireAfterAccess() {
            return expireAfterAccess;
        }

        public void setExpireAfterAccess(long expireAfterAccess) {
            this.expireAfterAccess = expireAfterAccess;
        }

        public long getExpireAfterWrite() {
            return expireAfterWrite;
        }

        public void setExpireAfterWrite(long expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(long maximumSize) {
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

        public boolean isEnableKeyPrefix() {
            return enableKeyPrefix;
        }

        public void setEnableKeyPrefix(boolean enableKeyPrefix) {
            this.enableKeyPrefix = enableKeyPrefix;
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
    }

    public static class Extension {

        private String cacheLock;

        private int cacheLockSize = 128;

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

        public int getCacheLockSize() {
            return cacheLockSize;
        }

        public void setCacheLockSize(int cacheLockSize) {
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
    }
}