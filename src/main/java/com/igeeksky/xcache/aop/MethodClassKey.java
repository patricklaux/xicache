package com.igeeksky.xcache.aop;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-14
 */
public class MethodClassKey {

    private final Method method;

    private final Class<?> targetClass;

    public MethodClassKey(Method method, Class<?> targetClass) {
        this.method = method;
        this.targetClass = targetClass;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MethodClassKey that)) return false;
        return Objects.equals(getMethod(), that.getMethod()) && Objects.equals(getTargetClass(), that.getTargetClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getTargetClass());
    }

}