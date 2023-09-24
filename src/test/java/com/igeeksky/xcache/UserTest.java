package com.igeeksky.xcache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-24
 */
class UserTest {

    @Test
    void testToString() {
        User user = new User();
        user.setId("001");
        user.setName("John");
        user.setAge(18);

        System.out.println(user);
        assertEquals("{\"id\":\"001\", \"name\":\"John\", \"age\":18}", user.toString());
    }
}