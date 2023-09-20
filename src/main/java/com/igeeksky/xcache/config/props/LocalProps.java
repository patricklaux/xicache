package com.igeeksky.xcache.config.props;

import com.igeeksky.xcache.config.CacheConstants;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class LocalProps {

    private String cacheStore;
    private String storeName;
    private Integer initialCapacity;
    private Long maximumSize;
    private Long maximumWeight;
    private String keyStrength;
    private String valueStrength;
    private Long expireAfterWrite;
    private Long expireAfterAccess;
    private String compressor;
    private String keyConvertor;
    private String valueSerializer;
    private String valueCompressor;
    private Boolean enableRandomTtl;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getInitialCapacity() {
        return initialCapacity;
    }

    public void setInitialCapacity(Integer initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    public Long getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Long maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Long getMaximumWeight() {
        return maximumWeight;
    }

    public void setMaximumWeight(Long maximumWeight) {
        this.maximumWeight = maximumWeight;
    }

    public String getKeyStrength() {
        return keyStrength;
    }

    public void setKeyStrength(String keyStrength) {
        this.keyStrength = keyStrength;
    }

    public String getValueStrength() {
        return valueStrength;
    }

    public void setValueStrength(String valueStrength) {
        this.valueStrength = valueStrength;
    }

    public Long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(Long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public Long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(Long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
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

    public Boolean getEnableRandomTtl() {
        return enableRandomTtl;
    }

    public void setEnableRandomTtl(Boolean enableRandomTtl) {
        this.enableRandomTtl = enableRandomTtl;
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
