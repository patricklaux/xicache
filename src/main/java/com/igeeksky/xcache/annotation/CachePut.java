package com.igeeksky.xcache.annotation;

import java.lang.annotation.*;

/**
 * <p>更新源数据后需更新缓存数据，此时使用 CachePut 注解。</p>
 * 使用此注解的方法总会被调用，方法返回结果会被放入缓存（更新缓存数据）。
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CachePut {

    String key() default "";

    String value() default "";

    String condition() default "";

    String name();

    Class<?> keyType() default Object.class;

    Class<?> valueType();

    Class<?>[] valueParams() default {};

}
