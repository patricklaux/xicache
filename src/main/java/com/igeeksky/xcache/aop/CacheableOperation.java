package com.igeeksky.xcache.aop;

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
            String trim = StringUtils.trim(key);
            if (StringUtils.hasLength(trim)) {
                this.key = trim;
            }
            return this;
        }

        public Builder value(String value) {
            String trim = StringUtils.trim(value);
            if (StringUtils.hasLength(trim)) {
                this.value = trim;
            }
            return this;
        }

        public Builder condition(String condition) {
            String trim = StringUtils.trim(condition);
            if (StringUtils.hasLength(trim)) {
                this.condition = trim;
            }
            return this;
        }

        public Builder unless(String unless) {
            String trim = StringUtils.trim(unless);
            if (StringUtils.hasLength(trim)) {
                this.unless = trim;
            }
            return this;
        }

        public CacheableOperation build() {
            return new CacheableOperation(this);
        }
    }

}
