package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.CacheMessageConsumer;
import com.igeeksky.xcache.extension.sync.CacheMessagePublisher;
import com.igeeksky.xcache.extension.sync.CacheSyncProvider;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class LettuceCacheSyncProvider implements CacheSyncProvider, AutoCloseable {

    private final LettuceCacheMessagePublisher publisher;

    private final LettuceCacheMessageListener listener;

    private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;

    public LettuceCacheSyncProvider(LettuceConnectionManager connectionManager) {
        this.listener = new LettuceCacheMessageListener();
        pubSubConnection = connectionManager.getPubSubConnection();
        pubSubConnection.addListener(this.listener);
        this.publisher = new LettuceCacheMessagePublisher(pubSubConnection.reactive());
    }

    @Override
    public CacheMessagePublisher getPublisher(String channel) {
        pubSubConnection.reactive().subscribe(channel).subscribe();
        return publisher;
    }

    @Override
    public <K, V> void register(String channel, CacheMessageConsumer consumer) {
        listener.register(channel, consumer);
    }

    @Override
    public void close() {
        if (this.pubSubConnection != null && this.pubSubConnection.isOpen()) {
            this.pubSubConnection.close();
        }
    }

    private static class LettuceCacheMessagePublisher implements CacheMessagePublisher {
        private final RedisPubSubReactiveCommands<String, byte[]> pubSubReactiveCommands;

        public LettuceCacheMessagePublisher(RedisPubSubReactiveCommands<String, byte[]> pubSubReactiveCommands) {
            this.pubSubReactiveCommands = pubSubReactiveCommands;
        }

        @Override
        public void publish(String channel, byte[] message) {
            pubSubReactiveCommands.publish(channel, message).subscribe();
        }
    }

}
