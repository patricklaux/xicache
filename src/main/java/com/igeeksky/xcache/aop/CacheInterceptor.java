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

    private CacheOperationSource source;

    public CacheInterceptor(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // TODO 执行具体缓存注解拦截实现
        return null;
    }

    public void setCacheOperationSource(CacheOperationSource source) {
        this.source = source;
    }
}
