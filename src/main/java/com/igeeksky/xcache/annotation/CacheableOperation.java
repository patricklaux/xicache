package com.igeeksky.xcache.annotation;

import com.igeeksky.xtool.core.lang.StringUtils;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-14
 */
public class CacheableOperation extends CacheOperation {

    private final String key;

    private final String value;

    private final String condition;

    private final String unless;

    protected CacheableOperation(Builder builder) {
        super(builder);
        this.key = builder.key;
        this.value = builder.value;
        this.condition = builder.condition;
        this.unless = builder.unless;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getCondition() {
        return condition;
    }

    public String getUnless() {
        return unless;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends CacheOperation.Builder {

        private String key;

        private String value;

        private String condition;

        private String unless;

        public Builder key(String key) {
            this.key = StringUtils.trim(key);
            return this;
        }

        public Builder value(String value) {
            this.value = StringUtils.trim(value);
            return this;
        }

        public Builder condition(String condition) {
            this.condition = StringUtils.trim(condition);
            return this;
        }

        public Builder unless(String unless) {
            this.unless = StringUtils.trim(unless);
            return this;
        }

        public CacheableOperation build() {
            return new CacheableOperation(this);
        }
    }

}
