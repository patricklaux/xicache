package com.igeeksky.xcache.extension.redis.config.props;

import com.igeeksky.xcache.autoconfigure.lettuce.RedisType;
import com.igeeksky.xcache.extension.redis.config.ClusterConfig;
import com.igeeksky.xcache.extension.redis.config.StandaloneConfig;
import com.igeeksky.xtool.core.lang.Assert;
import com.igeeksky.xtool.core.lang.StringUtils;
import io.lettuce.core.SslVerifyMode;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class Lettuce {

    private String id;

    private String host = "localhost";

    private int port = 6379;

    private int database = 0;

    private String username;

    private String password;

    private String clientName;

    private Long timeout;

    private Boolean ssl;

    private Boolean startTls;

    private Boolean verifyPeer;

    private SslVerifyMode sslVerifyMode;

    private RedisType redisType = RedisType.SINGLE;

    private List<String> replicas;

    private String readFrom;

    private Sentinel sentinel;

    private Cluster cluster;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public Boolean getStartTls() {
        return startTls;
    }

    public void setStartTls(Boolean startTls) {
        this.startTls = startTls;
    }

    public Boolean getVerifyPeer() {
        return verifyPeer;
    }

    public void setVerifyPeer(Boolean verifyPeer) {
        this.verifyPeer = verifyPeer;
    }

    public SslVerifyMode getSslVerifyMode() {
        return sslVerifyMode;
    }

    public void setSslVerifyMode(SslVerifyMode sslVerifyMode) {
        this.sslVerifyMode = sslVerifyMode;
    }

    public RedisType getRedisType() {
        return redisType;
    }

    public void setRedisType(RedisType redisType) {
        this.redisType = redisType;
    }

    public List<String> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<String> replicas) {
        this.replicas = replicas;
    }

    public String getReadFrom() {
        return readFrom;
    }

    public void setReadFrom(String readFrom) {
        this.readFrom = readFrom;
    }

    public Sentinel getSentinel() {
        return sentinel;
    }

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public StandaloneConfig createStandaloneConfig() {
        StandaloneConfig config = new StandaloneConfig();
        Assert.hasText(this.id, "id must not be null or empty");
        config.setId(StringUtils.trim(this.id));

        if (StringUtils.hasText(this.host)) {
            config.setHost(StringUtils.trim(this.host));
        }
        config.setPort(this.port);
        config.setDatabase(this.database);
        if (StringUtils.hasText(this.username)) {
            config.setUsername(StringUtils.trim(this.username));
        }
        if (StringUtils.hasText(this.password)) {
            config.setPassword(this.password);
        }
        if (StringUtils.hasText(this.clientName)) {
            config.setClientName(this.clientName);
        }
        if (this.timeout != null) {
            config.setTimeout(this.timeout);
        }
        if (this.ssl != null) {
            config.setSsl(this.ssl);
        }
        if (this.startTls != null) {
            config.setStartTls(this.startTls);
        }
        if (this.verifyPeer != null) {
            config.setVerifyPeer(this.verifyPeer);
        }
        if (this.sslVerifyMode != null) {
            config.setSslVerifyMode(this.sslVerifyMode);
        }
        if (this.redisType != null) {
            config.setRedisType(redisType);
        }
        if (this.replicas != null) {
            config.setReplicas(this.replicas);
        }
        if (StringUtils.hasText(readFrom)) {
            config.setReadFrom(this.readFrom);
        }
        return config;
    }

    public ClusterConfig createClusterConfig() {
        ClusterConfig config = new ClusterConfig();
        Assert.hasText(this.id, "id must not be null or empty");
        config.setId(StringUtils.trim(this.id));
        if (StringUtils.hasText(this.username)) {
            config.setUsername(StringUtils.trim(this.username));
        }
        if (StringUtils.hasText(this.password)) {
            config.setPassword(this.password);
        }
        if (StringUtils.hasText(this.clientName)) {
            config.setClientName(this.clientName);
        }
        if (this.timeout != null) {
            config.setTimeout(this.timeout);
        }
        if (this.ssl != null) {
            config.setSsl(this.ssl);
        }
        if (this.startTls != null) {
            config.setStartTls(this.startTls);
        }
        if (this.verifyPeer != null) {
            config.setVerifyPeer(this.verifyPeer);
        }
        if (this.sslVerifyMode != null) {
            config.setSslVerifyMode(this.sslVerifyMode);
        }
        if (StringUtils.hasText(readFrom)) {
            config.setReadFrom(this.readFrom);
        }
        config.setCluster(cluster);
        return config;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("id='" + id + "'")
                .add("host='" + host + "'")
                .add("port=" + port)
                .add("database=" + database)
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("clientName='" + clientName + "'")
                .add("timeout=" + timeout)
                .add("ssl=" + ssl)
                .add("startTls=" + startTls)
                .add("verifyPeer=" + verifyPeer)
                .add("sslVerifyMode=" + sslVerifyMode)
                .add("redisType=" + redisType)
                .add("replicas=" + replicas)
                .add("readFrom=" + readFrom)
                .add("sentinel=" + sentinel)
                .add("cluster=" + cluster)
                .toString();
    }
}