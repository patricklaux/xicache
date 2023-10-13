package com.igeeksky.xcache.aop;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-13
 */
public class CacheAnnotationParser {

    public Collection<CacheOperation> parseCacheAnnotations(Method method) {

        return null;
    }

    public boolean isCandidateClass(Class<?> targetClass) {

        return true;
    }

}
