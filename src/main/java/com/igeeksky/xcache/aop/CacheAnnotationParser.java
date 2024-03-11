package com.igeeksky.xcache.aop;


import com.igeeksky.xcache.annotation.*;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-13
 */
public class CacheAnnotationParser {

    public static Collection<CacheOperation> parseCacheAnnotations(Method method) {
        List<CacheOperation> result = new ArrayList<>();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if (cacheable != null) {
            CacheableOperation.Builder builder = CacheableOperation.builder();
            process(builder, cacheable);

            builder.key(cacheable.key());
            builder.value(cacheable.value());
            builder.condition(cacheable.condition());
            builder.unless(cacheable.unless());
            result.add(builder.build());
        }

        // TODO 解析其它注解
        CacheableAll cacheableAll = method.getAnnotation(CacheableAll.class);
        if (cacheableAll != null) {
            CacheableAllOperation.Builder builder = CacheableAllOperation.builder();
        }

        return CollectionUtils.isEmpty(result) ? null : result;
    }

    private static void process(CacheOperation.Builder builder, Cacheable cacheable) {
        builder.name(cacheable.name());
        builder.keyType(cacheable.keyType());
        builder.valueType(cacheable.valueType());
        builder.valueParams(cacheable.valueParams());
    }

}