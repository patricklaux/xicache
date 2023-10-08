package com.igeeksky.xcache.extension.contains;

import com.igeeksky.xcache.config.props.CacheProps;

/**
 * 无操作类工厂，test方法始终返回true
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-07-26
 */
public class AlwaysTruePredicateProvider implements ContainsPredicateProvider {

    public static final AlwaysTruePredicateProvider INSTANCE = new AlwaysTruePredicateProvider();

    @Override
    public <K> ContainsPredicate<K> get(Class<K> keyType, CacheProps cacheProps) {
        return AlwaysTrueContainsPredicate.getInstance();
    }

    @Override
    public void close() {
        // do nothing
    }
}