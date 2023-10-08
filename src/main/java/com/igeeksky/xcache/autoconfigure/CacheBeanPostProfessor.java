package com.igeeksky.xcache.autoconfigure;

import com.igeeksky.xcache.Cache;
import com.igeeksky.xcache.XcacheManager;
import com.igeeksky.xcache.annotation.CacheAutowired;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-09
 */
@Component
public class CacheBeanPostProfessor implements BeanPostProcessor {

    private final XcacheManager xcacheManager;

    @Lazy
    public CacheBeanPostProfessor(XcacheManager xcacheManager) {
        this.xcacheManager = xcacheManager;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            CacheAutowired annotation = field.getAnnotation(CacheAutowired.class);
            if (annotation != null) {
                String name = annotation.name();
                Class<?> keyType = annotation.keyType();
                Class<?> valueType = annotation.valueType();
                Class<?>[] valueParams = annotation.valueParams();

                Cache<?, ?> cache = xcacheManager.getOrCreateCache(name, keyType, valueType, valueParams);
                field.setAccessible(true);
                ReflectionUtils.setField(field, bean, cache);
            }
        });
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}
