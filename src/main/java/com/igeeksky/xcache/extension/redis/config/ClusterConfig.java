package com.igeeksky.xcache.extension.redis.config;

import com.igeeksky.xcache.extension.redis.config.props.Cluster;
import io.lettuce.core.SslVerifyMode;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.resource.ClientResources;

import java.nio.charset.Charset;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class ClusterConfig {

    private String id;

    private Charset charset;

    private String username;

    private String password;

    private String clientName;

    private long timeout;

    private boolean ssl;

    private boolean startTls;

    private boolean verifyPeer;

    private SslVerifyMode sslVerifyMode;

    private String readFrom;

    private Cluster cluster;

    private ClusterClientOptions clientOptions;

    private ClientResources clientResources;

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

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public void setStartTls(boolean startTls) {
        this.startTls = startTls;
    }

    public boolean isVerifyPeer() {
        return verifyPeer;
    }

    public void setVerifyPeer(boolean verifyPeer) {
        this.verifyPeer = verifyPeer;
    }

    public SslVerifyMode getSslVerifyMode() {
        return sslVerifyMode;
    }

    public void setSslVerifyMode(SslVerifyMode sslVerifyMode) {
        this.sslVerifyMode = sslVerifyMode;
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

    public ClusterClientOptions getClientOptions() {
        return clientOptions;
    }

    public void setClientOptions(ClusterClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    public ClientResources getClientResources() {
        return clientResources;
    }

    public void setClientResources(ClientResources clientResources) {
        this.clientResources = clientResources;
    }

}