package com.igeeksky.xcache.config;

import com.igeeksky.xcache.config.props.CacheProps;
import com.igeeksky.xcache.config.props.ExtensionProps;
import com.igeeksky.xcache.config.props.LocalProps;
import com.igeeksky.xcache.config.props.RemoteProps;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>配置工具类</p>
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class CacheConfigUtil {

    /**
     * <p> 用户模板配置 覆盖 模板默认配置，生成用户自定义模板配置 </p>
     * <p> 用户单个缓存配置 覆盖 用户自定义模板配置，生成最终配置 </p>
     * <p> 用户配置项如果为空，则使用模板配置项(或默认配置项) </p>
     * <p> String类型，如果不希望使用该配置项，可以配置为 “none” <p>
     */
    public static CacheProps copyProperties(CacheProps from, CacheProps to) {
        to.setName(from.getName());
        to.setTemplate(from.getTemplate());

        String charset = StringUtils.trim(from.getCharset());
        if (StringUtils.hasLength(charset)) {
            to.setCharset(charset);
        }
        String cacheType = StringUtils.trim(from.getCacheType());
        if (StringUtils.hasLength(cacheType)) {
            to.setCacheType(cacheType);
        }

        copyProperties(from.getLocal(), to.getLocal());
        copyProperties(from.getRemote(), to.getRemote());
        copyProperties(from.getExtension(), to.getExtension());

        to.getMetadata().putAll(from.getMetadata());
        return to;
    }

    private static void copyProperties(LocalProps from, LocalProps to) {
        String cacheStore = StringUtils.trim(from.getCacheStore());
        if (StringUtils.hasLength(cacheStore)) {
            to.setCacheStore(cacheStore);
        }

        String storeName = StringUtils.trim(from.getStoreName());
        if (StringUtils.hasLength(storeName)) {
            to.setStoreName(storeName);
        }

        Integer initialCapacity = from.getInitialCapacity();
        if (initialCapacity != null) {
            to.setInitialCapacity(initialCapacity);
        }

        Long maximumSize = from.getMaximumSize();
        if (maximumSize != null) {
            to.setMaximumSize(maximumSize);
        }

        Long maximumWeight = from.getMaximumWeight();
        if (maximumWeight != null) {
            to.setMaximumWeight(maximumWeight);
        }

        Long expireAfterWrite = from.getExpireAfterWrite();
        if (expireAfterWrite != null) {
            to.setExpireAfterWrite(expireAfterWrite);
        }

        Long expireAfterAccess = from.getExpireAfterAccess();
        if (expireAfterAccess != null) {
            to.setExpireAfterAccess(expireAfterAccess);
        }

        String keyStrength = StringUtils.trim(from.getKeyStrength());
        if (StringUtils.hasLength(keyStrength)) {
            to.setKeyStrength(keyStrength);
        }

        String valueStrength = StringUtils.trim(from.getValueStrength());
        if (StringUtils.hasLength(valueStrength)) {
            to.setValueStrength(valueStrength);
        }

        String valueSerializer = StringUtils.trim(from.getValueSerializer());
        if (StringUtils.hasLength(valueSerializer)) {
            to.setValueSerializer(valueSerializer);
        }

        String valueCompressor = StringUtils.trim(from.getValueCompressor());
        if (StringUtils.hasLength(valueCompressor)) {
            to.setValueCompressor(valueCompressor);
        }

        Boolean enableRandomTtl = from.getEnableRandomTtl();
        if (enableRandomTtl != null) {
            to.setEnableRandomTtl(enableRandomTtl);
        }

        Boolean enableKeyPrefix = from.getEnableKeyPrefix();
        if (enableKeyPrefix != null) {
            to.setEnableKeyPrefix(enableKeyPrefix);
        }

        Boolean enableNullValue = from.getEnableNullValue();
        if (enableNullValue != null) {
            to.setEnableNullValue(enableNullValue);
        }

        Boolean enableCompressValue = from.getEnableCompressValue();
        if (enableCompressValue != null) {
            to.setEnableCompressValue(enableCompressValue);
        }

        Boolean enableSerializeValue = from.getEnableSerializeValue();
        if (enableSerializeValue != null) {
            to.setEnableSerializeValue(enableSerializeValue);
        }
    }

    private static void copyProperties(RemoteProps from, RemoteProps to) {
        String cacheStore = StringUtils.trim(from.getCacheStore());
        if (StringUtils.hasLength(cacheStore)) {
            to.setCacheStore(cacheStore);
        }

        String storeName = StringUtils.trim(from.getStoreName());
        if (StringUtils.hasLength(storeName)) {
            to.setStoreName(storeName);
        }

        Long expireAfterWrite = from.getExpireAfterWrite();
        if (expireAfterWrite != null) {
            to.setExpireAfterWrite(expireAfterWrite);
        }

        String valueSerializer = StringUtils.trim(from.getValueSerializer());
        if (StringUtils.hasLength(valueSerializer)) {
            to.setValueSerializer(valueSerializer);
        }

        String valueCompressor = StringUtils.trim(from.getValueCompressor());
        if (StringUtils.hasLength(valueCompressor)) {
            to.setValueCompressor(valueCompressor);
        }

        Boolean enableRandomTtl = from.getEnableRandomTtl();
        if (enableRandomTtl != null) {
            to.setEnableRandomTtl(enableRandomTtl);
        }

        Boolean enableKeyPrefix = from.getEnableKeyPrefix();
        if (enableKeyPrefix != null) {
            to.setEnableKeyPrefix(enableKeyPrefix);
        }

        Boolean enableNullValue = from.getEnableNullValue();
        if (enableNullValue != null) {
            to.setEnableNullValue(enableNullValue);
        }

        Boolean enableCompressValue = from.getEnableCompressValue();
        if (enableCompressValue != null) {
            to.setEnableCompressValue(enableCompressValue);
        }
    }

    private static void copyProperties(ExtensionProps from, ExtensionProps to) {

        String cacheLock = StringUtils.trim(from.getCacheLock());
        if (StringUtils.hasLength(cacheLock)) {
            to.setCacheLock(cacheLock);
        }
    }

    public static <K, V> CacheConfig<K, V> createConfig(String application, CacheProps cacheProps,
                                                        Class<K> keyType, Class<V> valueType) {
        CacheConfig<K, V> config = new CacheConfig<>();
        config.setName(cacheProps.getName());
        config.setApplication(application);
        config.setCharset(getCharset(cacheProps));
        config.setKeyType(keyType);
        config.setValueType(valueType);
        config.setLocalConfig(createLocalConfig(cacheProps.getLocal()));
        config.setRemoteConfig(createRemoteConfig(cacheProps.getRemote()));
        config.setMetadata(cacheProps.getMetadata());
        return config;
    }

    private static <K, V> LocalConfig<K, V> createLocalConfig(LocalProps local) {
        LocalConfig<K, V> config = new LocalConfig<>();
        config.setStoreName(StringUtils.trim(local.getStoreName()));
        config.setInitialCapacity(local.getInitialCapacity());
        config.setMaximumSize(local.getMaximumSize());
        config.setMaximumWeight(local.getMaximumWeight());
        config.setKeyStrength(local.getKeyStrength());
        config.setValueStrength(local.getValueStrength());
        config.setExpireAfterWrite(local.getExpireAfterWrite());
        config.setExpireAfterAccess(local.getExpireAfterAccess());
        config.setEnableRandomTtl(local.getEnableRandomTtl());
        config.setEnableNullValue(local.getEnableNullValue());
        config.setEnableCompressValue(local.getEnableCompressValue());
        config.setEnableSerializeValue(local.getEnableSerializeValue());
        return config;
    }

    private static <K, V> RemoteConfig<K, V> createRemoteConfig(RemoteProps remote) {
        RemoteConfig<K, V> config = new RemoteConfig<>();
        config.setStoreName(remote.getStoreName());
        config.setExpireAfterWrite(remote.getExpireAfterWrite());
        config.setEnableKeyPrefix(remote.getEnableKeyPrefix());
        config.setEnableRandomTtl(remote.getEnableRandomTtl());
        config.setEnableNullValue(remote.getEnableNullValue());
        config.setEnableCompressValue(remote.getEnableCompressValue());
        return config;
    }

    private static Charset getCharset(CacheProps cacheProps) {
        String charsetName = StringUtils.toUpperCase(cacheProps.getCharset());
        if (StringUtils.hasLength(charsetName)) {
            return Charset.forName(charsetName);
        }
        return StandardCharsets.UTF_8;
    }

    public static CacheProps defaultCacheProps() {
        CacheProps cacheProps = new CacheProps();
        cacheProps.setTemplate("T0");
        // TODO 完善缓存配置默认值
        cacheProps.setLocal(defaultLocalProps());
        cacheProps.setRemote(defaultRemoteProps());
        cacheProps.setExtension(defaultExtensionProps());
        return cacheProps;
    }

    private static LocalProps defaultLocalProps() {
        LocalProps localProps = new LocalProps();
        localProps.setCacheStore(CacheConstants.LOCAL_CACHE_STORE);
        // TODO 完善本地缓存默认配置
        return localProps;
    }

    private static RemoteProps defaultRemoteProps() {
        RemoteProps remoteProps = new RemoteProps();
        remoteProps.setCacheStore(CacheConstants.REMOTE_CACHE_STORE);
        remoteProps.setStoreName(CacheConstants.REMOTE_STORE_NAME);
        remoteProps.setExpireAfterWrite(CacheConstants.REMOTE_EXPIRE_AFTER_WRITE);
        remoteProps.setValueSerializer(CacheConstants.REMOTE_VALUE_SERIALIZER);
        remoteProps.setValueCompressor(CacheConstants.REMOTE_VALUE_COMPRESSOR);
        remoteProps.setEnableRandomTtl(CacheConstants.REMOTE_ENABLE_RANDOM_TTL);
        remoteProps.setEnableKeyPrefix(CacheConstants.REMOTE_ENABLE_KEY_PREFIX);
        remoteProps.setEnableNullValue(CacheConstants.REMOTE_ENABLE_NULL_VALUE);
        remoteProps.setEnableCompressValue(CacheConstants.REMOTE_ENABLE_COMPRESS_VALUE);
        return remoteProps;
    }

    private static ExtensionProps defaultExtensionProps() {
        ExtensionProps extensionProps = new ExtensionProps();
        extensionProps.setCacheLock(CacheConstants.EXTENSION_CACHE_LOCK);
        // TODO 完善扩展配置默认值

        return extensionProps;
    }

}