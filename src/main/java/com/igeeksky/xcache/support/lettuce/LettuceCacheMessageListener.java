package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.CacheMessageConsumer;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-12
 */
public class LettuceCacheMessageListener implements RedisPubSubListener<String, byte[]> {

    private static final Logger log = LoggerFactory.getLogger(LettuceCacheMessageListener.class);

    private final ConcurrentMap<String, CacheMessageConsumer> consumerMap = new ConcurrentHashMap<>();

    public void register(String channel, CacheMessageConsumer consumer) {
        this.consumerMap.put(channel, consumer);
    }

    @Override
    public void message(String channel, byte[] message) {
        CacheMessageConsumer consumer = consumerMap.get(channel);
        if (consumer != null) {
            consumer.onMessage(message);
            return;
        }
        log.error("No consumer to process this message. chan:[{}]", channel);
    }

    @Override
    public void message(String pattern, String channel, byte[] message) {
        CacheMessageConsumer consumer = consumerMap.get(channel);
        if (consumer != null) {
            consumer.onMessage(message);
            return;
        }
        log.error("No consumer to process this message. pattern:[{}],  chan:[{}]", pattern, channel);
    }

    @Override
    public void subscribed(String channel, long count) {
        if (log.isDebugEnabled()) {
            log.debug("subscribed-channel:[{}], count:[{}]", channel, count);
        }
    }

    @Override
    public void psubscribed(String pattern, long count) {
        if (log.isDebugEnabled()) {
            log.debug("psubscribed-pattern:[{}], count:[{}]", pattern, count);
        }
    }

    @Override
    public void unsubscribed(String channel, long count) {
        if (log.isDebugEnabled()) {
            log.debug("unsubscribed-channel:[{}], count:[{}]", channel, count);
        }
    }

    @Override
    public void punsubscribed(String pattern, long count) {
        if (log.isDebugEnabled()) {
            log.debug("punsubscribed-pattern:[{}], count:[{}]", pattern, count);
        }
    }
}