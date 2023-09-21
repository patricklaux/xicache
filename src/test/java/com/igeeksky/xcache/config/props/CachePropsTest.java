package com.igeeksky.xcache.config.props;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-21
 */
class CachePropsTest {

    @Test
    void testClone() {
        CacheProps props = new CacheProps();
        props.setName("user");
        props.getLocal().setEnableKeyPrefix(false);
        props.getLocal().setExpireAfterAccess(100L);

        CacheProps clone = props.clone();

        props.setName("order");
        props.getLocal().setEnableKeyPrefix(true);
        props.getLocal().setExpireAfterAccess(101L);

        System.out.println(clone.getName());
        System.out.println(clone.getLocal().getEnableKeyPrefix());
        System.out.println(clone.getLocal().getExpireAfterAccess());

        assertNotEquals(props.getName(), clone.getName());
        assertNotEquals(props.getLocal().getEnableKeyPrefix(), clone.getLocal().getEnableKeyPrefix());
        assertNotEquals(props.getLocal().getExpireAfterAccess(), clone.getLocal().getExpireAfterAccess());
    }

}