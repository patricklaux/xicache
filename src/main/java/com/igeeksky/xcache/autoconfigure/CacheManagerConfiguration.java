package com.igeeksky.xcache.autoconfigure;

import com.igeeksky.xcache.CacheManager;
import com.igeeksky.xcache.XcacheManager;
import com.igeeksky.xcache.aop.ProxyCacheConfiguration;
import com.igeeksky.xcache.autoconfigure.holder.*;
import com.igeeksky.xcache.config.CacheConfigException;
import com.igeeksky.xcache.config.props.CacheProps;
import com.igeeksky.xcache.config.props.TemplateId;
import com.igeeksky.xtool.core.lang.Assert;
import com.igeeksky.xtool.core.lang.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CacheManager 自动配置
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-29
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(ProxyCacheConfiguration.class)
public class CacheManagerConfiguration {

    private final CacheProperties cacheProperties;

    CacheManagerConfiguration(CacheProperties cacheProperties) {
        System.out.println(cacheProperties);
        this.cacheProperties = cacheProperties;
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    CacheManager cacheManager(ObjectProvider<LocalCacheStoreProviderHolder> localStoreHolders,
                              ObjectProvider<RemoteCacheStoreProviderHolder> remoteStoreHolders,
                              ObjectProvider<KeyConvertorProviderHolder> keyConvertorHolders,
                              ObjectProvider<SerializerProviderHolder> serializerHolders,
                              ObjectProvider<CacheSyncProviderHolder> syncHolders,
                              ObjectProvider<CacheStatProviderHolder> statHolders,
                              ObjectProvider<CacheLockProviderHolder> lockHolders,
                              ObjectProvider<CacheMonitorProviderHolder> monitorHolders,
                              ObjectProvider<ContainsPredicateProviderHolder> predicateHolders,
                              ObjectProvider<CompressorProviderHolder> compressorHolders) {

        String application = cacheProperties.getApplication();

        Map<TemplateId, CacheProps> templates = new HashMap<>();
        putTemplate(templates, TemplateId.T0, cacheProperties.getT0());
        putTemplate(templates, TemplateId.T1, cacheProperties.getT1());
        putTemplate(templates, TemplateId.T2, cacheProperties.getT2());
        putTemplate(templates, TemplateId.T3, cacheProperties.getT3());
        putTemplate(templates, TemplateId.T4, cacheProperties.getT4());
        putTemplate(templates, TemplateId.T5, cacheProperties.getT5());
        putTemplate(templates, TemplateId.T6, cacheProperties.getT6());
        putTemplate(templates, TemplateId.T7, cacheProperties.getT7());
        putTemplate(templates, TemplateId.T8, cacheProperties.getT8());
        putTemplate(templates, TemplateId.T9, cacheProperties.getT9());

        Map<String, CacheProps> cachePropsMap = new HashMap<>();
        List<CacheProps> caches = cacheProperties.getCaches();
        for (CacheProps props : caches) {
            String name = StringUtils.trim(props.getName());
            Assert.hasLength(name, new CacheConfigException("cache-name must not be null or empty"));
            cachePropsMap.put(name, props);
        }

        XcacheManager xcacheManager = new XcacheManager(application, templates, cachePropsMap);

        for (LocalCacheStoreProviderHolder holder : localStoreHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (RemoteCacheStoreProviderHolder holder : remoteStoreHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (KeyConvertorProviderHolder holder : keyConvertorHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (SerializerProviderHolder holder : serializerHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (CacheSyncProviderHolder holder : syncHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (CacheStatProviderHolder holder : statHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (CacheLockProviderHolder holder : lockHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (CacheMonitorProviderHolder holder : monitorHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (ContainsPredicateProviderHolder holder : predicateHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        for (CompressorProviderHolder holder : compressorHolders) {
            holder.getAll().forEach(xcacheManager::addProvider);
        }

        return xcacheManager;
    }

    private static void putTemplate(Map<TemplateId, CacheProps> templates, TemplateId id, CacheProps cacheProps) {
        if (cacheProps != null) {
            templates.put(id, cacheProps);
        }
    }

}