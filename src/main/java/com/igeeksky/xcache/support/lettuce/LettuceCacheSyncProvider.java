package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.CacheMessageConsumer;
import com.igeeksky.xcache.extension.sync.CacheMessagePublisher;
import com.igeeksky.xcache.extension.sync.CacheSyncConsumer;
import com.igeeksky.xcache.extension.sync.CacheSyncProvider;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class LettuceCacheSyncProvider implements CacheSyncProvider {

    private final LettuceCacheMessageListener listener;

    private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;

    private final CacheMessagePublisher publisher;

    public LettuceCacheSyncProvider(LettuceCacheSyncConfig config) {
        this.listener = config.getListener();
        ComposedRedisCodec redisCodec = ComposedRedisCodec.getInstance(config.getCharset());
        AbstractRedisClient redisClient = config.getRedisClient();
        if (redisClient instanceof RedisClient) {
            pubSubConnection = ((RedisClient) redisClient).connectPubSub(redisCodec);
        } else {
            pubSubConnection = ((RedisClusterClient) redisClient).connectPubSub(redisCodec);
        }
        pubSubConnection.addListener(listener);
        this.publisher = new LettuceCacheMessagePublisher(pubSubConnection);
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

    private static class LettuceCacheMessagePublisher implements CacheMessagePublisher {
        private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;

        public LettuceCacheMessagePublisher(StatefulRedisPubSubConnection<String, byte[]> pubSubConnection) {
            this.pubSubConnection = pubSubConnection;
        }

        @Override
        public void publish(String channel, byte[] message) {
            pubSubConnection.reactive().publish(channel, message).subscribe();
        }
    }

}
