package com.igeeksky.xcache.extension.redis;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class RedisNode {

    private String host;

    private int port;

    public RedisNode() {
    }

    public RedisNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
