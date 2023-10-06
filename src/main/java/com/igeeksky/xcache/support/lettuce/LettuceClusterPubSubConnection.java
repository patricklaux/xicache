package com.igeeksky.xcache.support.lettuce;

import com.igeeksky.xcache.extension.redis.RedisPubSubConnection;
import com.igeeksky.xcache.extension.redis.RedisPubSubListener;
import com.igeeksky.xtool.core.io.IOUtils;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import io.lettuce.core.cluster.pubsub.RedisClusterPubSubListener;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.reactive.RedisClusterPubSubReactiveCommands;
import reactor.core.publisher.Mono;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-01
 */
public class LettuceClusterPubSubConnection implements RedisPubSubConnection {

    private final StatefulRedisClusterPubSubConnection<String, byte[]> connection;
    private final RedisClusterPubSubReactiveCommands<String, byte[]> pubSubCommands;

    public LettuceClusterPubSubConnection(StatefulRedisClusterPubSubConnection<String, byte[]> connection) {
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
        connection.addListener(new RedisClusterPubSubListener<String, byte[]>() {

            @Override
            public void message(RedisClusterNode node, String channel, byte[] message) {
                listener.message(channel, message);
            }

            @Override
            public void message(RedisClusterNode node, String pattern, String channel, byte[] message) {
                listener.message(pattern, channel, message);
            }

            @Override
            public void subscribed(RedisClusterNode node, String channel, long count) {
                listener.subscribed(channel, count);
            }

            @Override
            public void psubscribed(RedisClusterNode node, String pattern, long count) {
                listener.psubscribed(pattern, count);
            }

            @Override
            public void unsubscribed(RedisClusterNode node, String channel, long count) {
                listener.unsubscribed(channel, count);
            }

            @Override
            public void punsubscribed(RedisClusterNode node, String pattern, long count) {
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