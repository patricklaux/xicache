package com.igeeksky.xcache.autoconfigure.caffeine;

import com.igeeksky.xcache.autoconfigure.XcacheManagerConfiguration;
import com.igeeksky.xcache.autoconfigure.holder.LocalCacheStoreProviderHolder;
import com.igeeksky.xcache.support.caffeine.CaffeineCacheStoreProvider;
import com.igeeksky.xcache.support.caffeine.CaffeineExpiryProvider;
import com.igeeksky.xcache.support.caffeine.CaffeineWeigherProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(XcacheManagerConfiguration.class)
class CaffeineAutoConfiguration {

    public static final String CAFFEINE_BEAN_ID = "caffeineCacheStoreProvider";

    @Bean
    LocalCacheStoreProviderHolder caffeineCacheStoreProviderHolder(ObjectProvider<CaffeineExpiryProvider> expiryProviders,
                                                                   ObjectProvider<CaffeineWeigherProvider> weigherProviders) {

        CaffeineExpiryProvider expiryProvider = expiryProviders.getIfAvailable();
        CaffeineWeigherProvider weigherProvider = weigherProviders.getIfAvailable();
        CaffeineCacheStoreProvider provider = new CaffeineCacheStoreProvider(expiryProvider, weigherProvider);

        LocalCacheStoreProviderHolder holder = new LocalCacheStoreProviderHolder();
        holder.put(CAFFEINE_BEAN_ID, provider);
        return holder;
    }

}
