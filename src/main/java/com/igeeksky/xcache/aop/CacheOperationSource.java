package com.igeeksky.xcache.aop;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-13
 */
public class CacheOperationSource {

    private static final Logger logger = LoggerFactory.getLogger(CacheOperationSource.class);

    public static final String CACHE_OPERATION_SOURCE_BEAN_NAME = "com.igeeksky.xcache.aop.cacheOperationSource";

    private final Set<CacheAnnotationParser> annotationParsers;

    private final Map<Object, Collection<CacheOperation>> attributeCache = new ConcurrentHashMap<>(1024);

    public CacheOperationSource() {
        this.annotationParsers = null;
    }

    public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }

        Object cacheKey = getCacheKey(method, targetClass);
        Collection<CacheOperation> cached = this.attributeCache.get(cacheKey);

        // if (cached != null) {
        //     return (cached != NULL_CACHING_ATTRIBUTE ? cached : null);
        // } else {
        //     Collection<CacheOperation> cacheOps = computeCacheOperations(method, targetClass);
        //     if (cacheOps != null) {
        //         if (logger.isTraceEnabled()) {
        //             logger.trace("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
        //         }
        //         this.attributeCache.put(cacheKey, cacheOps);
        //     } else {
        //         this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
        //     }
        //     return cacheOps;
        // }
        return null;
    }

    private Object getCacheKey(Method method, Class<?> targetClass) {
        return "null";
    }
}
