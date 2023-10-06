package com.igeeksky.xcache.extension.redis;

import com.igeeksky.xcache.extension.CacheMessageConsumer;
import com.igeeksky.xcache.extension.sync.CacheMessagePublisher;
import com.igeeksky.xcache.extension.sync.CacheSyncManager;
import com.igeeksky.xcache.support.lettuce.LettuceConnectionFactory;
import com.igeeksky.xtool.core.io.IOUtils;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class RedisCacheSyncManager implements CacheSyncManager {

    private final RedisCacheMessagePublisher publisher;

    private final RedisCacheMessageListener listener;

    private final RedisPubSubConnection pubSubConnection;

    public RedisCacheSyncManager(LettuceConnectionFactory factory) {
        this.listener = new RedisCacheMessageListener();
        this.pubSubConnection = factory.getPubSubConnection();
        this.pubSubConnection.addListener(this.listener);
        this.publisher = new RedisCacheMessagePublisher(pubSubConnection);
    }

    @Override
    public CacheMessagePublisher getPublisher(String channel) {
        pubSubConnection.subscribe(channel).subscribe();
        return publisher;
    }

    @Override
    public void register(String channel, CacheMessageConsumer consumer) {
        listener.register(channel, consumer);
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(this.pubSubConnection);
    }

    private static class RedisCacheMessagePublisher implements CacheMessagePublisher {
        private final RedisPubSubConnection pubSubConnection;

        public RedisCacheMessagePublisher(RedisPubSubConnection pubSubConnection) {
            this.pubSubConnection = pubSubConnection;
        }

        @Override
        public void publish(String channel, byte[] message) {
            pubSubConnection.publish(channel, message).subscribe();
        }
    }

}
