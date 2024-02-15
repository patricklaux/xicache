package com.igeeksky.xcache.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-06
 */
class HostAndPortTest {

    private static final Logger log = LoggerFactory.getLogger(HostAndPortTest.class);

    @Test
    void testErrorHostAndPort() {
        Exception err = null;
        try {
            new HostAndPort("2:0:1");
        } catch (Exception e) {
            err = e;
        }
        Assertions.assertTrue(err instanceof IllegalArgumentException);
        Assertions.assertEquals(err.getMessage(), "node:[2:0:1] can't convert to HostAndPort.");
    }

    @Test
    void getHostAndPort() {
        String hp = "127.0.0.1:6379";
        HostAndPort hostAndPort = new HostAndPort(hp);
        String host = hostAndPort.getHost();
        int port = hostAndPort.getPort();
        Assertions.assertEquals(host, "127.0.0.1");
        Assertions.assertEquals(port, 6379);
    }

}