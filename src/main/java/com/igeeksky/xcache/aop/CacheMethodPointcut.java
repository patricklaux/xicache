package com.igeeksky.xcache.aop;

import com.igeeksky.xtool.core.collection.CollectionUtils;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-12
 */
public class CacheMethodPointcut extends StaticMethodMatcherPointcut {

    private final CacheOperationSource source;

    public CacheMethodPointcut(String[] basePackages, CacheOperationSource source) {
        this.source = source;
        this.setClassFilter(new CacheOperationSourceClassFilter(source, basePackages));
    }

    @Override
    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
        // 判断类是否匹配（类是否位于指定扫描的包）
        if (!this.getClassFilter().matches(targetClass)) {
            return false;
        }
        return (source != null && CollectionUtils.isNotEmpty(source.getCacheOperations(method, targetClass)));
    }

    private static class CacheOperationSourceClassFilter implements ClassFilter {

        private final String[] basePackages;
        private final CacheOperationSource source;

        public CacheOperationSourceClassFilter(CacheOperationSource source, String[] basePackages) {
            this.source = source;
            this.basePackages = basePackages;
        }

        @Override
        public boolean matches(Class<?> clazz) {
            String name = clazz.getName();
            // TODO exclude

            // include
            for (String pkg : basePackages) {
                if (name.startsWith(pkg)) {
                    return true;
                }
            }
            return false;
        }
    }

}
