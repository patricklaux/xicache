package com.igeeksky.xcache.annotation;

import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-12
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Reflective
public @interface Cacheable {

    String key() default "";

    String value() default "";

    String condition() default "";

    String unless() default "";

    /**
     * 缓存名称
     */
    @AliasFor("value")
    String name();

    /**
     * 键的类型
     */
    Class<?> keyType() default Object.class;

    /**
     * 值的类型
     */
    Class<?> valueType();

    /**
     * 值的泛型的类型
     */
    Class<?>[] valueParams() default {};

}
