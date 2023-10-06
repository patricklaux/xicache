package com.igeeksky.xcache.support.lettuce.config.props;

import com.igeeksky.xcache.config.HostAndPort;
import com.igeeksky.xcache.support.lettuce.config.LettuceClusterConfig;
import com.igeeksky.xcache.support.lettuce.config.LettuceGenericConfig;
import com.igeeksky.xcache.support.lettuce.config.LettuceSentinelConfig;
import com.igeeksky.xcache.support.lettuce.config.LettuceStandaloneConfig;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import com.igeeksky.xtool.core.lang.Assert;
import com.igeeksky.xtool.core.lang.StringUtils;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.SslVerifyMode;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class Lettuce {

    private String id;

    private String charset;

    private Cluster cluster;

    private Sentinel sentinel;

    private Standalone standalone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Sentinel getSentinel() {
        return sentinel;
    }

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    public Standalone getStandalone() {
        return standalone;
    }

    public void setStandalone(Standalone standalone) {
        this.standalone = standalone;
    }

    public LettuceStandaloneConfig createStandaloneConfig() {
        Assert.hasText(this.id, "lettuce:connections:id must not be null or empty");

        LettuceStandaloneConfig config = new LettuceStandaloneConfig();
        config.setId(StringUtils.trim(this.id));

        this.charset = StringUtils.trim(this.charset);
        if (StringUtils.hasLength(this.charset)) {
            config.setCharset(Charset.forName(this.charset));
        }

        String master = StringUtils.trim(standalone.getMaster());
        if (StringUtils.hasLength(master)) {
            config.setMaster(new HostAndPort(master));
        }

        String readFrom = StringUtils.trim(standalone.getReadFrom());
        if (StringUtils.hasLength(readFrom)) {
            config.setReadFrom(ReadFrom.valueOf(readFrom));
        }

        config.setReplicas(convert(standalone.getReplicas()));
        config.setClientOptions(standalone.getClientOptions());

        setGeneric(standalone, config);

        return config;
    }

    public LettuceSentinelConfig createSentinelConfig() {
        Assert.hasText(this.id, "lettuce:connections:id must not be null or empty");

        LettuceSentinelConfig config = new LettuceSentinelConfig();
        config.setId(StringUtils.trim(this.id));

        this.charset = StringUtils.trim(this.charset);
        if (StringUtils.hasLength(this.charset)) {
            config.setCharset(Charset.forName(this.charset));
        }

        String masterId = StringUtils.trim(sentinel.getMasterId());
        Assert.hasText(masterId, () -> "Id:[" + this.id + "] sentinel:master-id must not be null or empty");
        config.setMasterId(masterId);

        List<HostAndPort> nodes = convert(sentinel.getNodes());
        Assert.notEmpty(nodes, () -> "Id:[" + this.id + "] sentinel:nodes must not be empty");
        config.setSentinels(nodes);

        String readFrom = StringUtils.trim(sentinel.getReadFrom());
        if (StringUtils.hasLength(readFrom)) {
            config.setReadFrom(ReadFrom.valueOf(readFrom));
        }

        String sentinelUsername = StringUtils.trim(sentinel.getUsername());
        if (StringUtils.hasLength(sentinelUsername)) {
            config.setSentinelUsername(sentinelUsername);
        }

        String sentinelPassword = StringUtils.trim(sentinel.getPassword());
        if (StringUtils.hasLength(sentinelPassword)) {
            config.setSentinelPassword(sentinelPassword);
        }

        config.setClientOptions(sentinel.getClientOptions());

        setGeneric(sentinel, config);

        return config;
    }

    public LettuceClusterConfig createClusterConfig() {
        Assert.hasText(this.id, "lettuce:connections:id must not be null or empty");

        LettuceClusterConfig config = new LettuceClusterConfig();
        config.setId(StringUtils.trim(this.id));

        this.charset = StringUtils.trim(this.charset);
        if (StringUtils.hasLength(this.charset)) {
            config.setCharset(Charset.forName(this.charset));
        }

        List<HostAndPort> nodes = convert(cluster.getNodes());
        Assert.notEmpty(nodes, () -> "Id:[" + this.id + "] cluster:nodes must not be empty");
        config.setNodes(nodes);

        String readFrom = StringUtils.trim(cluster.getReadFrom());
        if (StringUtils.hasLength(readFrom)) {
            config.setReadFrom(ReadFrom.valueOf(readFrom));
        }

        config.setClientOptions(cluster.getClientOptions());

        setGeneric(cluster, config);

        return config;
    }

    private static void setGeneric(Generic original, LettuceGenericConfig config) {
        String username = StringUtils.trim(original.getUsername());
        if (StringUtils.hasLength(username)) {
            config.setUsername(username);
        }

        String password = StringUtils.trim(original.getPassword());
        if (StringUtils.hasLength(password)) {
            config.setPassword(password);
        }

        config.setDatabase(original.getDatabase());

        String clientName = StringUtils.trim(original.getClientName());
        if (StringUtils.hasLength(clientName)) {
            config.setClientName(clientName);
        }

        Boolean ssl = original.getSsl();
        if (ssl != null) {
            config.setSsl(ssl);
        }

        Boolean startTls = original.getStartTls();
        if (startTls != null) {
            config.setStartTls(startTls);
        }

        Boolean verifyPeer = original.getVerifyPeer();
        if (verifyPeer != null) {
            config.setVerifyPeer(verifyPeer);
        }

        String sslVerifyMode = StringUtils.toUpperCase(original.getSslVerifyMode());
        if (StringUtils.hasLength(sslVerifyMode)) {
            config.setSslVerifyMode(SslVerifyMode.valueOf(sslVerifyMode));
        }

        Long timeout = original.getTimeout();
        if (timeout != null) {
            config.setTimeout(timeout);
        }
    }

    private static List<HostAndPort> convert(List<String> sources) {
        List<HostAndPort> nodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sources)) {
            for (String node : sources) {
                nodes.add(new HostAndPort(node));
            }
        }
        return nodes;
    }

}