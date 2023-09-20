package com.igeeksky.xcache.config.props;

import com.igeeksky.xcache.config.CacheConstants;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class LocalProps {

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

    public static LocalProps createDefault() {
        LocalProps localProps = new LocalProps();
        localProps.setCacheStore(CacheConstants.LOCAL_CACHE_STORE);
        // TODO 完善本地缓存默认配置
        return localProps;
    }

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

    public LocalProps deepClone() {
        LocalProps clone = new LocalProps();
        clone.setCacheStore(this.getCacheStore());
        // TODO 深度克隆 LocalProps
        return clone;
    }
}
