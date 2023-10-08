package com.igeeksky.xcache.autoconfigure;

import com.igeeksky.xcache.autoconfigure.holder.ContainsPredicateProviderHolder;
import com.igeeksky.xcache.extension.contains.AlwaysTruePredicateProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-09
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(XcacheManagerConfiguration.class)
class AlwaysTrueContainsPredicateConfiguration {

    public static final String ALWAYS_TRUE_CONTAINS_PREDICATE_PROVIDER_ID = "alwaysTruePredicateProvider";

    @Bean
    ContainsPredicateProviderHolder containsPredicateProviderHolder() {
        ContainsPredicateProviderHolder holder = new ContainsPredicateProviderHolder();
        holder.put(ALWAYS_TRUE_CONTAINS_PREDICATE_PROVIDER_ID, AlwaysTruePredicateProvider.INSTANCE);
        return holder;
    }

}
