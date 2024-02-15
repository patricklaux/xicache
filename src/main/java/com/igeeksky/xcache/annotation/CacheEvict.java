package com.igeeksky.xcache.annotation;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-12
 */
public @interface CacheEvict {

    String name() default "";

    Class<?> keyType();

}
