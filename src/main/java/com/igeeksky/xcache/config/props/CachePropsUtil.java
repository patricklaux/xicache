package com.igeeksky.xcache.config.props;

import com.igeeksky.xtool.core.lang.StringUtils;

/**
 * <p> 用户配置 覆盖 默认配置，生成最终配置 </p>
 * <p> 用户配置项如果为空，则使用模板配置项 </p>
 * <p> String类型，如果不希望使用该配置项，可以配置为 “none” <p>
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-20
 */
public class CachePropsUtil {

    public static CacheProps merge(CacheProps from, CacheProps template) {
        // TODO 完善合并配置
        CacheProps to = template.deepClone();
        to.setName(StringUtils.trim(from.getName()));
        to.setTemplate(from.getTemplate());

        String charset = StringUtils.trim(from.getCharset());
        if (StringUtils.hasLength(charset)) {
            to.setCharset(charset);
        }

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

    }

    private static void merge(ExtensionProps from, ExtensionProps to) {

    }

}
