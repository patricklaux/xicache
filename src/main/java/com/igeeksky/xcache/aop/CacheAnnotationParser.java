package com.igeeksky.xcache.aop;


import com.igeeksky.xcache.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-13
 */
public class CacheAnnotationParser {

    private static final Set<Class<? extends Annotation>> CACHE_OPERATION_ANNOTATIONS = new LinkedHashSet<>(8);

    static {
        CACHE_OPERATION_ANNOTATIONS.add(Cacheable.class);
        CACHE_OPERATION_ANNOTATIONS.add(CacheableAll.class);
        CACHE_OPERATION_ANNOTATIONS.add(CacheEvict.class);
        CACHE_OPERATION_ANNOTATIONS.add(CacheEvictAll.class);
        CACHE_OPERATION_ANNOTATIONS.add(CachePut.class);
        CACHE_OPERATION_ANNOTATIONS.add(CachePutAll.class);
    }

    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        List<CacheOperation> result = new ArrayList<>();
        for (Class<? extends Annotation> clazz : CACHE_OPERATION_ANNOTATIONS) {
            Annotation annotation = method.getAnnotation(clazz);

        }
        return null;
    }

}