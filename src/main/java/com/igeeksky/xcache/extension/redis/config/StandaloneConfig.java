package com.igeeksky.xcache.extension.redis.config;

import com.igeeksky.xcache.autoconfigure.lettuce.RedisType;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslVerifyMode;
import io.lettuce.core.resource.ClientResources;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class StandaloneConfig {

    private String id;

    private Charset charset;

    private String host = "localhost";

    private int port = 6379;

    private int database = 0;

    private String username;

    private String password;

    private String clientName;

    private long timeout;

    private boolean ssl = false;

    private boolean startTls = false;

    private boolean verifyPeer = false;

    private SslVerifyMode sslVerifyMode = SslVerifyMode.CA;

    private RedisType redisType = RedisType.SINGLE;

    private List<String> replicas;

    private String readFrom = "UPSTREAM";

    private ClientOptions clientOptions;

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

    public ClientOptions getClientOptions() {
        return clientOptions;
    }

    public void setClientOptions(ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    public ClientResources getClientResources() {
        return clientResources;
    }

    public void setClientResources(ClientResources clientResources) {
        this.clientResources = clientResources;
    }
}
