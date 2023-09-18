package com.igeeksky.xcache.config;

import com.igeeksky.xcache.extension.monitor.CacheMonitor;
import com.igeeksky.xcache.extension.monitor.CacheMonitorProxy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-15
 */
class CacheConfigTest {

    @Test
    void getName() {
        List<CacheMonitor<String>> monitors = new ArrayList<>();
        monitors.add(new CacheMonitorProxy<>());
        CacheConfig<String, String> config = new CacheConfig<>("order");
        config.setMonitors(monitors);

        CacheConfig<String, String> clone = config.clone();
        config.setName("user");
        assertEquals("order", clone.getName());

        config.getMonitors().add(new CacheMonitorProxy<>());

        assertEquals(2, config.getMonitors().size());
        assertEquals(2, clone.getMonitors().size());
    }

    @Test
    void getMonitors() {
    }
}