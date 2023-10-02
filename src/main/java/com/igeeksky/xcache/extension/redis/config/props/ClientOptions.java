package com.igeeksky.xcache.extension.redis.config.props;

import io.lettuce.core.SocketOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.protocol.DecodeBufferPolicy;
import io.lettuce.core.protocol.ProtocolVersion;

import java.nio.charset.Charset;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class ClientOptions {

    private boolean autoReconnect;

    private boolean cancelCommandsOnReconnectFailure;

    private DecodeBufferPolicy decodeBufferPolicy;

    private io.lettuce.core.ClientOptions.DisconnectedBehavior disconnectedBehavior;

    private boolean pingBeforeActivateConnection;

    private ProtocolVersion protocolVersion;

    private boolean publishOnScheduler;

    private int requestQueueSize;

    private Charset scriptCharset;

    private SocketOptions socketOptions;

    private SslOptions sslOptions;

    private boolean suspendReconnectOnProtocolFailure;

    private TimeoutOptions timeoutOptions;

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public boolean isCancelCommandsOnReconnectFailure() {
        return cancelCommandsOnReconnectFailure;
    }

    public void setCancelCommandsOnReconnectFailure(boolean cancelCommandsOnReconnectFailure) {
        this.cancelCommandsOnReconnectFailure = cancelCommandsOnReconnectFailure;
    }

    public DecodeBufferPolicy getDecodeBufferPolicy() {
        return decodeBufferPolicy;
    }

    public void setDecodeBufferPolicy(DecodeBufferPolicy decodeBufferPolicy) {
        this.decodeBufferPolicy = decodeBufferPolicy;
    }

    public boolean isPingBeforeActivateConnection() {
        return pingBeforeActivateConnection;
    }

    public void setPingBeforeActivateConnection(boolean pingBeforeActivateConnection) {
        this.pingBeforeActivateConnection = pingBeforeActivateConnection;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean isPublishOnScheduler() {
        return publishOnScheduler;
    }

    public void setPublishOnScheduler(boolean publishOnScheduler) {
        this.publishOnScheduler = publishOnScheduler;
    }

    public int getRequestQueueSize() {
        return requestQueueSize;
    }

    public void setRequestQueueSize(int requestQueueSize) {
        this.requestQueueSize = requestQueueSize;
    }

    public Charset getScriptCharset() {
        return scriptCharset;
    }

    public void setScriptCharset(Charset scriptCharset) {
        this.scriptCharset = scriptCharset;
    }

    public SocketOptions getSocketOptions() {
        return socketOptions;
    }

    public void setSocketOptions(SocketOptions socketOptions) {
        this.socketOptions = socketOptions;
    }

    public SslOptions getSslOptions() {
        return sslOptions;
    }

    public void setSslOptions(SslOptions sslOptions) {
        this.sslOptions = sslOptions;
    }

    public boolean isSuspendReconnectOnProtocolFailure() {
        return suspendReconnectOnProtocolFailure;
    }

    public void setSuspendReconnectOnProtocolFailure(boolean suspendReconnectOnProtocolFailure) {
        this.suspendReconnectOnProtocolFailure = suspendReconnectOnProtocolFailure;
    }

    public TimeoutOptions getTimeoutOptions() {
        return timeoutOptions;
    }

    public void setTimeoutOptions(TimeoutOptions timeoutOptions) {
        this.timeoutOptions = timeoutOptions;
    }
}
