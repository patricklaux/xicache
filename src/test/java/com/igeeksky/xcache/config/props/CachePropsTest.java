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
        props.getRemote().setEnableKeyPrefix(false);
        props.getLocal().setExpireAfterAccess(100L);

        CacheProps clone = props.clone();

        props.setName("order");
        props.getRemote().setEnableKeyPrefix(true);
        props.getLocal().setExpireAfterAccess(101L);

        System.out.println(clone.getName() + " - " + props.getName());
        System.out.println(clone.getRemote().getEnableKeyPrefix() + " - " + props.getRemote().getEnableKeyPrefix());
        System.out.println(clone.getLocal().getExpireAfterAccess() + " - " + props.getLocal().getExpireAfterAccess());

        assertNotEquals(props.getName(), clone.getName());
        assertNotEquals(props.getRemote().getEnableKeyPrefix(), clone.getRemote().getEnableKeyPrefix());
        assertNotEquals(props.getLocal().getExpireAfterAccess(), clone.getLocal().getExpireAfterAccess());
    }

}