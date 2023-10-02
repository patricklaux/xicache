package com.igeeksky.xcache.config.props;

import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class LocalProps implements Cloneable {

    private String cacheStore;
    private String storeName;
    private Integer initialCapacity;
    private Long maximumSize;
    private Long maximumWeight;
    private Long expireAfterWrite;
    private Long expireAfterAccess;
    private String keyStrength;
    private String valueStrength;
    private String valueSerializer;
    private String valueCompressor;
    private Boolean enableRandomTtl;
    private Boolean enableNullValue;

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

    public Boolean getEnableNullValue() {
        return enableNullValue;
    }

    public void setEnableNullValue(Boolean enableNullValue) {
        this.enableNullValue = enableNullValue;
    }

    @Override
    public LocalProps clone() throws CloneNotSupportedException {
        return (LocalProps) super.clone();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("cacheStore='" + cacheStore + "'")
                .add("storeName='" + storeName + "'")
                .add("initialCapacity=" + initialCapacity)
                .add("maximumSize=" + maximumSize)
                .add("maximumWeight=" + maximumWeight)
                .add("expireAfterWrite=" + expireAfterWrite)
                .add("expireAfterAccess=" + expireAfterAccess)
                .add("keyStrength='" + keyStrength + "'")
                .add("valueStrength='" + valueStrength + "'")
                .add("valueSerializer='" + valueSerializer + "'")
                .add("valueCompressor='" + valueCompressor + "'")
                .add("enableRandomTtl=" + enableRandomTtl)
                .add("enableNullValue=" + enableNullValue)
                .toString();
    }
}
