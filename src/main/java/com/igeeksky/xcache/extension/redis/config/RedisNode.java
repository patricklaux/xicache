package com.igeeksky.xcache.extension.redis.config;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class RedisNode {

    private final String host;

    private final int port;

    public RedisNode(String node) {
        String[] hostAndPort = node.split(":");
        this.host = hostAndPort[0];
        this.port = Integer.parseInt(hostAndPort[1]);
    }

    public RedisNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}