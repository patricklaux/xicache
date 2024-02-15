package com.igeeksky.xcache.extension.statistic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-19
 */
class CacheStatMessageTest {

    @Test
    void testToString() {
        CacheStatistics noop = new CacheStatistics();
        noop.setClears(5);
        noop.setPuts(4);
        noop.setRemovals(3);
        noop.setMisses(2);
        noop.setHits(1);
        String noopString = noop.toString();
        System.out.println(noopString + "\n");
        Assertions.assertEquals("{\"hits\":1, \"misses\":2, \"puts\":4, \"removals\":3, \"clears\":5, \"hitPercentage\":0.333333}", noopString);

        CacheStatistics local = new CacheStatistics();
        local.setClears(10);
        local.setPuts(9);
        local.setRemovals(8);
        local.setMisses(7);
        local.setHits(6);
        String localString = local.toString();
        System.out.println(localString + "\n");
        Assertions.assertEquals("{\"hits\":6, \"misses\":7, \"puts\":9, \"removals\":8, \"clears\":10, \"hitPercentage\":0.461538}", localString);

        CacheStatistics remote = new CacheStatistics();
        remote.setClears(15);
        remote.setPuts(14);
        remote.setRemovals(13);
        remote.setMisses(12);
        remote.setHits(11);
        String remoteString = remote.toString();
        System.out.println(remoteString + "\n");
        Assertions.assertEquals("{\"hits\":11, \"misses\":12, \"puts\":14, \"removals\":13, \"clears\":15, \"hitPercentage\":0.478261}", remoteString);

        CacheStatMessage message = new CacheStatMessage("user", "shop");
        message.setLoads(16);
        message.setNoop(noop);
        message.setLocal(local);
        message.setRemote(remote);

        String messageString = message.toString();
        System.out.println(messageString);
        Assertions.assertEquals("{\"name\":\"user\", \"application\":\"shop\", \"loads\":16, \"noop\":{\"hits\":1, \"misses\":2, \"puts\":4, \"removals\":3, \"clears\":5, \"hitPercentage\":0.333333}, \"local\":{\"hits\":6, \"misses\":7, \"puts\":9, \"removals\":8, \"clears\":10, \"hitPercentage\":0.461538}, \"remote\":{\"hits\":11, \"misses\":12, \"puts\":14, \"removals\":13, \"clears\":15, \"hitPercentage\":0.478261}}", messageString);
    }
}