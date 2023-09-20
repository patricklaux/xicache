package com.igeeksky.xcache.config.props;

import com.igeeksky.xcache.config.CacheConstants;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class RemoteProps {

    private String cacheStore;
    private String storeName;
    private Long expireAfterWrite;
    private String keyConvertor;
    private String valueSerializer;
    private String valueCompressor;
    private Boolean enableKeyPrefix;
    private Boolean enableNullValue;
    private Boolean enableCompressValue;

    public static RemoteProps createDefault() {
        RemoteProps remoteProps = new RemoteProps();
        remoteProps.setCacheStore(CacheConstants.REMOTE_CACHE_STORE);
        remoteProps.setStoreName(CacheConstants.REMOTE_STORE_NAME);
        remoteProps.setExpireAfterWrite(CacheConstants.REMOTE_EXPIRE_AFTER_WRITE);
        remoteProps.setKeyConvertor(CacheConstants.REMOTE_KEY_CONVERTOR);
        remoteProps.setValueSerializer(CacheConstants.REMOTE_VALUE_SERIALIZER);
        remoteProps.setValueCompressor(CacheConstants.REMOTE_VALUE_COMPRESSOR);
        remoteProps.setEnableKeyPrefix(CacheConstants.REMOTE_ENABLE_KEY_PREFIX);
        remoteProps.setEnableNullValue(CacheConstants.REMOTE_ENABLE_NULL_VALUE);
        remoteProps.setEnableCompressValue(CacheConstants.REMOTE_ENABLE_COMPRESS_VALUE);
        return remoteProps;
    }

    public String getCacheStore() {
        return cacheStore;
    }

    public void setCacheStore(String cacheStore) {
        this.cacheStore = cacheStore;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(Long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public String getKeyConvertor() {
        return keyConvertor;
    }

    public void setKeyConvertor(String keyConvertor) {
        this.keyConvertor = keyConvertor;
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

    public RemoteProps deepClone() {
        RemoteProps clone = new RemoteProps();
        clone.setCacheStore(this.getCacheStore());
        // TODO 深度克隆 RemoteProps
        return clone;
    }
}
