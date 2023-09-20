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
     * //<p> 用户模板配置 覆盖 模板默认配置，生成用户自定义模板配置 </p>
     * <p> 用户单个缓存配置 覆盖 用户自定义模板配置，生成最终配置 </p>
     * <p> 用户配置项如果为空，则使用模板配置项 </p>
     * <p> String类型，如果不希望使用该配置项，可以配置为 “none” <p>
     */
    public static CacheProps merge(CacheProps from, CacheProps template) {
        // TODO 完善合并配置
        // 1. 创建一个模板配置的克隆对象
        CacheProps to = template.deepClone();
        // 2. 将用户的有效配置项覆盖克隆对象的值
        to.setName(from.getName());
        to.setTemplate(from.getTemplate());
        String charset = StringUtils.trim(from.getCharset());
        if (StringUtils.hasLength(charset)) {
            to.setCharset(charset);
        }
        String cacheType = StringUtils.trim(from.getCacheType());
        if (StringUtils.hasLength(cacheType)) {

        }
        //
        // String application = from.getApplication();

        merge(from.getLocal(), to.getLocal());
        merge(from.getRemote(), to.getRemote());
        merge(from.getExtension(), to.getExtension());
        return to;
    }

    private static void merge(LocalProps from, LocalProps to) {
        String cacheStore = StringUtils.trim(from.getCacheStore());
        if (StringUtils.hasLength(cacheStore)) {
            to.setCacheStore(cacheStore);
        }
    }

    private static void merge(RemoteProps from, RemoteProps to) {
        String cacheStore = StringUtils.trim(from.getCacheStore());
        if (StringUtils.hasLength(cacheStore)) {
            to.setCacheStore(cacheStore);
        }
    }

    private static void merge(ExtensionProps from, ExtensionProps to) {
        String cacheLock = StringUtils.trim(from.getCacheLock());
        if (StringUtils.hasLength(cacheLock)) {
            to.setCacheLock(cacheLock);
        }
    }

    public static <K, V> CacheConfig<K, V> createConfig(CacheProps cacheProps, Class<K> keyType, Class<V> valueType) {
        CacheConfig<K, V> config = new CacheConfig<>();
        config.setName(cacheProps.getName());
        config.setApplication(getApplication(cacheProps));
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

    private static String getApplication(CacheProps cacheProps) {
        String result = StringUtils.trim(cacheProps.getApplication());
        if (StringUtils.hasLength(result)) {
            return result;
        }
        throw new CacheConfigException("CacheName:[" + cacheProps.getName() + "]. application must not be empty or null.");
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
        remoteProps.setKeyConvertor(CacheConstants.REMOTE_KEY_CONVERTOR);
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