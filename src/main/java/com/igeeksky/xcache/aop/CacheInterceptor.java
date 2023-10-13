package com.igeeksky.xcache.aop;

import com.igeeksky.xcache.CacheManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-13
 */
public class CacheInterceptor implements MethodInterceptor, Serializable {

    public static final String CACHE_INTERCEPTOR_BEAN_NAME = "com.igeeksky.xcache.aop.cacheInterceptor";

    private final CacheManager cacheManager;

    public CacheInterceptor(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return null;
    }
}
