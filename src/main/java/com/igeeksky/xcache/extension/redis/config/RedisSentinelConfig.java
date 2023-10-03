package com.igeeksky.xcache.extension.redis.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class RedisSentinelConfig {

    /**
     * beanId
     */
    private String id;

    private Charset charset = StandardCharsets.UTF_8;

    private String readFrom = "MASTER";

    private String masterId;

    private String sentinelUsername;

    private String sentinelPassword;

    private final List<RedisNode> sentinels = new ArrayList<>();

    private final RedisGenericConfig generic = new RedisGenericConfig();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getReadFrom() {
        return readFrom;
    }

    public void setReadFrom(String readFrom) {
        this.readFrom = readFrom;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getSentinelUsername() {
        return sentinelUsername;
    }

    public void setSentinelUsername(String sentinelUsername) {
        this.sentinelUsername = sentinelUsername;
    }

    public String getSentinelPassword() {
        return sentinelPassword;
    }

    public void setSentinelPassword(String sentinelPassword) {
        this.sentinelPassword = sentinelPassword;
    }

    public List<RedisNode> getSentinels() {
        return sentinels;
    }

    public void addSentinel(RedisNode node) {
        this.sentinels.add(node);
    }

    public RedisGenericConfig getGeneric() {
        return generic;
    }

}
