package com.igeeksky.xcache.annotation;

import com.igeeksky.xtool.core.lang.Assert;
import com.igeeksky.xtool.core.lang.StringUtils;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-13
 */
public class CacheOperation {

    private final String name;

    private final Class<?> keyType;

    private final Class<?> valueType;

    private final Class<?>[] valueParams;

    protected CacheOperation(Builder builder) {
        this.name = builder.name;
        this.keyType = builder.keyType;
        this.valueType = builder.valueType;
        this.valueParams = builder.valueParams;
    }

    public String getName() {
        return name;
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public Class<?>[] getValueParams() {
        return valueParams;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;

        private Class<?> keyType = Object.class;

        private Class<?> valueType = Undefined.class;

        private Class<?>[] valueParams;

        public Builder name(String name) {
            String trim = StringUtils.trim(name);
            Assert.hasLength(trim, "Cache name must not be empty");
            this.name = trim;
            return this;
        }

        public Builder keyType(Class<?> keyType) {
            Assert.notNull(keyType, "keyType must not be null");
            this.keyType = keyType;
            return this;
        }

        public Builder valueType(Class<?> valueType) {
            Assert.notNull(valueType, "valueType must not be null");
            this.valueType = valueType;
            return this;
        }

        public Builder valueParams(Class<?>[] valueParams) {
            this.valueParams = valueParams;
            return this;
        }

        public CacheOperation build() {
            return new CacheOperation(this);
        }
    }

}