package com.igeeksky.xcache.autoconfigure;

import com.igeeksky.xcache.XcacheManager;
import com.igeeksky.xcache.autoconfigure.holder.*;
import com.igeeksky.xcache.config.CacheConfigException;
import com.igeeksky.xcache.config.props.CacheProps;
import com.igeeksky.xcache.config.props.TemplateId;
import com.igeeksky.xtool.core.lang.Assert;
import com.igeeksky.xtool.core.lang.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
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
public class XcacheManagerConfiguration {

    private final XcacheProperties xcacheProperties;

    XcacheManagerConfiguration(XcacheProperties xcacheProperties) {
        System.out.println(xcacheProperties);
        this.xcacheProperties = xcacheProperties;
    }

    @Bean
    @ConditionalOnMissingBean(XcacheManager.class)
    XcacheManager xcacheManager(ObjectProvider<LocalCacheStoreProviderHolder> localStoreHolders,
                                ObjectProvider<RemoteCacheStoreProviderHolder> remoteStoreHolders,
                                ObjectProvider<KeyConvertorProviderHolder> keyConvertorHolders,
                                ObjectProvider<SerializerProviderHolder> serializerHolders,
                                ObjectProvider<CacheSyncProviderHolder> syncHolders,
                                ObjectProvider<CacheStatProviderHolder> statHolders,
                                ObjectProvider<CacheLockProviderHolder> lockHolders,
                                ObjectProvider<CacheMonitorProviderHolder> monitorHolders,
                                ObjectProvider<ContainsPredicateProviderHolder> predicateHolders,
                                ObjectProvider<CompressorProviderHolder> compressorHolders
    ) {

        String application = xcacheProperties.getApplication();

        Map<TemplateId, CacheProps> templates = new HashMap<>();
        putTemplate(templates, TemplateId.T0, xcacheProperties.getT0());
        putTemplate(templates, TemplateId.T1, xcacheProperties.getT1());
        putTemplate(templates, TemplateId.T2, xcacheProperties.getT2());
        putTemplate(templates, TemplateId.T3, xcacheProperties.getT3());
        putTemplate(templates, TemplateId.T4, xcacheProperties.getT4());
        putTemplate(templates, TemplateId.T5, xcacheProperties.getT5());
        putTemplate(templates, TemplateId.T6, xcacheProperties.getT6());
        putTemplate(templates, TemplateId.T7, xcacheProperties.getT7());
        putTemplate(templates, TemplateId.T8, xcacheProperties.getT8());
        putTemplate(templates, TemplateId.T9, xcacheProperties.getT9());

        Map<String, CacheProps> cachePropsMap = new HashMap<>();
        List<CacheProps> caches = xcacheProperties.getCaches();
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