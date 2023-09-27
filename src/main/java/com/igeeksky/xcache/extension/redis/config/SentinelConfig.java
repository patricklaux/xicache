package com.igeeksky.xcache.extension.redis.config;

import com.igeeksky.xcache.extension.redis.RedisNode;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class SentinelConfig {

    private String username;

    private String password;

    private String master;

    private List<RedisNode> nodes;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public List<RedisNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<RedisNode> nodes) {
        this.nodes = nodes;
    }

}
