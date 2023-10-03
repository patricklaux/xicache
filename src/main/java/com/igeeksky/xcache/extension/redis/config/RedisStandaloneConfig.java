package com.igeeksky.xcache.extension.redis.config;

import com.igeeksky.xcache.autoconfigure.redis.lettuce.RedisType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class RedisStandaloneConfig {

    private String id;

    private Charset charset;

    private String host = "localhost";

    private int port = 6379;

    private String readFrom;

    private RedisType redisType = RedisType.SINGLE;

    private final List<RedisNode> replicas = new ArrayList<>();

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

    public String getReadFrom() {
        return readFrom;
    }

    public void setReadFrom(String readFrom) {
        this.readFrom = readFrom;
    }

    public RedisType getRedisType() {
        return redisType;
    }

    public void setRedisType(RedisType redisType) {
        this.redisType = redisType;
    }

    public List<RedisNode> getReplicas() {
        return replicas;
    }

    public void addReplica(RedisNode replica) {
        this.replicas.add(replica);
    }

    public RedisGenericConfig getGeneric() {
        return generic;
    }
}