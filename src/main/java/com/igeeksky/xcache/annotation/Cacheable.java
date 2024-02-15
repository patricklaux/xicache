package com.igeeksky.xcache.annotation;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-12
 */
public @interface Cacheable {

    String key() default "";

    String value() default "";

    String condition() default "";

    String unless() default "";

    String name();

    Class<?> keyType() default Object.class;

    Class<?> valueType();

    Class<?>[] valueParams() default {};

}
