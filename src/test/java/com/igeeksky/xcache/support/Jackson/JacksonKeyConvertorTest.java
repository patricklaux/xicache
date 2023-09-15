package com.igeeksky.xcache.support.Jackson;

import com.igeeksky.xcache.User;
import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.support.jackson.JacksonKeyConvertor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-15
 */
class JacksonKeyConvertorTest {

    @Test
    void apply() {
        User user = new User("John");
        KeyConvertor keyConvertor = JacksonKeyConvertor.getInstance();
        String key = keyConvertor.apply(user);
        assertEquals("{\"name\":\"John\"}", key);
    }

}