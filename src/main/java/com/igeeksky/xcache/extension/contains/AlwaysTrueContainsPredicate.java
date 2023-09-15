package com.igeeksky.xcache.extension.contains;

/**
 * 总是返回 true
 *
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-12
 */
public class AlwaysTrueContainsPredicate<K> implements ContainsPredicate<K> {

    private static final AlwaysTrueContainsPredicate<?> INSTANCE = new AlwaysTrueContainsPredicate<>();

    @SuppressWarnings("unchecked")
    public static <K> AlwaysTrueContainsPredicate<K> getINSTANCE() {
        return (AlwaysTrueContainsPredicate<K>) INSTANCE;
    }

    @Override
    public boolean test(String cacheName, K key) {
        return true;
    }

}
