package com.igeeksky.xcache.autoconfigure.caffeine;

import com.igeeksky.xcache.autoconfigure.XcacheManagerConfiguration;
import com.igeeksky.xcache.autoconfigure.holder.LocalCacheStoreHolder;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(XcacheManagerConfiguration.class)
public class CaffeineAutoConfiguration {

    LocalCacheStoreHolder localCacheStoreHolder() {
        // TODO Caffeine 配置文件及装配
        return null;
    }

}
