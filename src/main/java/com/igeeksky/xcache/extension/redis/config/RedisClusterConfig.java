package com.igeeksky.xcache.extension.redis.config;

import com.igeeksky.xcache.extension.redis.config.props.Cluster;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class RedisClusterConfig {

    private String id;

    private Charset charset;

    private String readFrom;

    private Cluster cluster;

    private final List<RedisNode> nodes = new ArrayList<>();

    private RedisGenericConfig generic;

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

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public List<RedisNode> getNodes() {
        return nodes;
    }

    public void addNode(RedisNode node) {
        this.nodes.add(node);
    }

    public RedisGenericConfig getGeneric() {
        return generic;
    }

    public void setGeneric(RedisGenericConfig generic) {
        this.generic = generic;
    }

}