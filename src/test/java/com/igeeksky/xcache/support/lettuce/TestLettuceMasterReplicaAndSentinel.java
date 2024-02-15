package com.igeeksky.xcache.support.lettuce;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import io.lettuce.core.sentinel.api.sync.RedisSentinelCommands;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TestLettuceMasterReplicaAndSentinel {

    public static void main(String[] args) {
        // sentinel
        RedisURI sentinelUri = RedisURI.builder()
                .withSentinel("192.168.1.31", 26379, "111111")    // 哨兵地址和密码
                .withSentinelMasterId("mymaster")
                .withPassword("123456".toCharArray())
                .build();
        RedisClient sentinelClient = RedisClient.create(sentinelUri);
        StatefulRedisSentinelConnection<String, String> sentinelConn = sentinelClient.connectSentinel();
        RedisSentinelCommands<String, String> sentinelCmd = sentinelConn.sync();
        System.out.println(sentinelCmd.info("sentinel"));
        List<Map<String, String>> masters = sentinelCmd.masters();
        System.out.println(masters);
        Map<String, String> mymaster = sentinelCmd.master("mymaster");
        System.out.println(mymaster.get("name") + "," + mymaster.get("ip") + "," + mymaster.get("port"));
        List<Map<String, String>> slaves = sentinelCmd.slaves("mymaster");
        slaves.forEach(x -> {
            System.out.println(x.get("ip") + ":" + x.get("port"));
        });

        StatefulRedisPubSubConnection<String, String> sentinelPubSubConn = sentinelClient.connectPubSub();// 需要设置主从密码
        sentinelPubSubConn.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void unsubscribed(String channel, long count) {
                System.out.printf("unsubscribed:%s%n", channel);
            }

            @Override
            public void subscribed(String channel, long count) {
                System.out.printf("subscribed:%s%n", channel);
            }

            @Override
            public void punsubscribed(String pattern, long count) {
            }

            @Override
            public void psubscribed(String pattern, long count) {
            }

            @Override
            public void message(String pattern, String channel, String message) {
            }

            @Override
            public void message(String channel, String message) {
                System.out.printf("message:[%s] -> [%s]%n", channel, message);
            }
        });
        RedisPubSubCommands<String, String> sentinelPubSubCmd = sentinelPubSubConn.sync();
        sentinelPubSubCmd.subscribe("__sentinel__:hello");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sentinelPubSubCmd.unsubscribe("__sentinel__:hello");

        // MasterReplica
        RedisClient client = RedisClient.create();
        ClientOptions options = ClientOptions.builder()
                .autoReconnect(true)    // 是否自动重连
                .pingBeforeActivateConnection(true)    // 连接激活之前是否执行PING命令
                .build();
        client.setOptions(options);
        StatefulRedisMasterReplicaConnection<String, String> conn = MasterReplica.connect(client, StringCodec.UTF8, sentinelUri);
        conn.setReadFrom(ReadFrom.ANY);        // 设置可从任何一个服务读数据

        for (int i = 0; i < 5; i++) {
            try {
                RedisCommands<String, String> cmd = conn.sync();
                cmd.set("aa", String.valueOf(i));
                System.out.print(cmd.get("aa") + "--");
                Stream.of(cmd.info("server").split("\n"))
                        .filter(x -> x.startsWith("tcp_port"))
                        .forEach(System.out::print);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}