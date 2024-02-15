package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.redis.RedisPubSubConnection;
import com.igeeksky.xcache.extension.redis.RedisPubSubListener;
import com.igeeksky.xtool.core.io.IOUtils;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import reactor.core.publisher.Mono;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettucePubSubConnection implements RedisPubSubConnection {

    private final StatefulRedisPubSubConnection<String, byte[]> connection;
    private final RedisPubSubReactiveCommands<String, byte[]> pubSubCommands;

    public LettucePubSubConnection(StatefulRedisPubSubConnection<String, byte[]> connection) {
        this.connection = connection;
        this.pubSubCommands = connection.reactive();
    }

    @Override
    public Mono<Void> psubscribe(String... patterns) {
        return pubSubCommands.psubscribe(patterns);
    }

    @Override
    public Mono<Void> punsubscribe(String... patterns) {
        return pubSubCommands.punsubscribe(patterns);
    }

    @Override
    public Mono<Void> subscribe(String... channels) {
        return pubSubCommands.subscribe(channels);
    }

    @Override
    public Mono<Void> unsubscribe(String... channels) {
        return pubSubCommands.unsubscribe(channels);
    }

    public void addListener(RedisPubSubListener listener) {
        connection.addListener(new io.lettuce.core.pubsub.RedisPubSubListener<String, byte[]>() {
            @Override
            public void message(String channel, byte[] message) {
                listener.message(channel, message);
            }

            @Override
            public void message(String pattern, String channel, byte[] message) {
                listener.message(pattern, channel, message);
            }

            @Override
            public void subscribed(String channel, long count) {
                listener.subscribed(channel, count);
            }

            @Override
            public void psubscribed(String pattern, long count) {
                listener.psubscribed(pattern, count);
            }

            @Override
            public void unsubscribed(String channel, long count) {
                listener.unsubscribed(channel, count);
            }

            @Override
            public void punsubscribed(String pattern, long count) {
                listener.punsubscribed(pattern, count);
            }
        });
    }

    @Override
    public Mono<Long> publish(String channel, byte[] message) {
        return this.pubSubCommands.publish(channel, message);
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(this.connection);
    }
}
