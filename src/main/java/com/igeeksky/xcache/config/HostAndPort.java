package com.igeeksky.xcache.config;

import com.igeeksky.xtool.core.lang.Assert;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class HostAndPort {

    private final String host;

    private final int port;

    public HostAndPort(String node) {
        String[] hp = node.split(":");
        Assert.isTrue(hp.length == 2, () -> "node:[" + node + "] can't convert to HostAndPort.");
        this.host = hp[0];
        this.port = Integer.parseInt(hp[1]);
    }

    public HostAndPort(String host, int port) {
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