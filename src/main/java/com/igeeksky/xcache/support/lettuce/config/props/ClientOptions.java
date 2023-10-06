package com.igeeksky.xcache.support.lettuce.config.props;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class ClientOptions {

    private Boolean autoReconnect;

    private Boolean cancelCommandsOnReconnectFailure;

    // 需要编程实现，无法通过配置处理
    // private DecodeBufferPolicy decodeBufferPolicy;

    private String disconnectedBehavior;

    private Boolean publishOnScheduler;

    private Boolean pingBeforeActivateConnection;

    private String protocolVersion;

    private Boolean suspendReconnectOnProtocolFailure;

    private Integer requestQueueSize;

    private String scriptCharset;

    private SslOptions sslOptions;

    private SocketOptions socketOptions;

    private TimeoutOptions timeoutOptions;

    public Boolean getAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(Boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public Boolean getCancelCommandsOnReconnectFailure() {
        return cancelCommandsOnReconnectFailure;
    }

    public void setCancelCommandsOnReconnectFailure(Boolean cancelCommandsOnReconnectFailure) {
        this.cancelCommandsOnReconnectFailure = cancelCommandsOnReconnectFailure;
    }

    public String getDisconnectedBehavior() {
        return disconnectedBehavior;
    }

    public void setDisconnectedBehavior(String disconnectedBehavior) {
        this.disconnectedBehavior = disconnectedBehavior;
    }

    public Boolean getPublishOnScheduler() {
        return publishOnScheduler;
    }

    public void setPublishOnScheduler(Boolean publishOnScheduler) {
        this.publishOnScheduler = publishOnScheduler;
    }

    public Boolean getPingBeforeActivateConnection() {
        return pingBeforeActivateConnection;
    }

    public void setPingBeforeActivateConnection(Boolean pingBeforeActivateConnection) {
        this.pingBeforeActivateConnection = pingBeforeActivateConnection;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Boolean getSuspendReconnectOnProtocolFailure() {
        return suspendReconnectOnProtocolFailure;
    }

    public void setSuspendReconnectOnProtocolFailure(Boolean suspendReconnectOnProtocolFailure) {
        this.suspendReconnectOnProtocolFailure = suspendReconnectOnProtocolFailure;
    }

    public Integer getRequestQueueSize() {
        return requestQueueSize;
    }

    public void setRequestQueueSize(Integer requestQueueSize) {
        this.requestQueueSize = requestQueueSize;
    }

    public String getScriptCharset() {
        return scriptCharset;
    }

    public void setScriptCharset(String scriptCharset) {
        this.scriptCharset = scriptCharset;
    }

    public SslOptions getSslOptions() {
        return sslOptions;
    }

    public void setSslOptions(SslOptions sslOptions) {
        this.sslOptions = sslOptions;
    }

    public SocketOptions getSocketOptions() {
        return socketOptions;
    }

    public void setSocketOptions(SocketOptions socketOptions) {
        this.socketOptions = socketOptions;
    }

    public TimeoutOptions getTimeoutOptions() {
        return timeoutOptions;
    }

    public void setTimeoutOptions(TimeoutOptions timeoutOptions) {
        this.timeoutOptions = timeoutOptions;
    }

    public static class SocketOptions {

        private Boolean tcpNoDelay;

        private Long connectTimeout;

        private KeepAliveOptions keepAlive;

        public Boolean getTcpNoDelay() {
            return tcpNoDelay;
        }

        public void setTcpNoDelay(Boolean tcpNoDelay) {
            this.tcpNoDelay = tcpNoDelay;
        }

        public Long getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Long connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public KeepAliveOptions getKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(KeepAliveOptions keepAlive) {
            this.keepAlive = keepAlive;
        }

    }

    public static class KeepAliveOptions {

        private Integer count;

        private Boolean enabled;

        private Long idle;

        private Long interval;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Long getIdle() {
            return idle;
        }

        public void setIdle(Long idle) {
            this.idle = idle;
        }

        public Long getInterval() {
            return interval;
        }

        public void setInterval(Long interval) {
            this.interval = interval;
        }

    }


    public static class TimeoutOptions {

        private Boolean timeoutCommands;

        private Boolean applyConnectionTimeout;

        private Long fixedTimeout;

        // 不同的命令采用不同的超时配置，需编程实现
        // private TimeoutSource timeoutSource

        public Boolean getTimeoutCommands() {
            return timeoutCommands;
        }

        public void setTimeoutCommands(Boolean timeoutCommands) {
            this.timeoutCommands = timeoutCommands;
        }

        public Boolean getApplyConnectionTimeout() {
            return applyConnectionTimeout;
        }

        public void setApplyConnectionTimeout(Boolean applyConnectionTimeout) {
            this.applyConnectionTimeout = applyConnectionTimeout;
        }

        public Long getFixedTimeout() {
            return fixedTimeout;
        }

        public void setFixedTimeout(Long fixedTimeout) {
            this.fixedTimeout = fixedTimeout;
        }

    }


    public static class SslOptions {

        private String sslProvider;

        private String keyStoreType;

        /**
         * URL
         */
        private String keystore;

        private String keystorePassword;

        /**
         * URL
         */
        private String truststore;

        private String truststorePassword;

        private List<String> protocols;

        private List<String> cipherSuites;

        private Long handshakeTimeout;

        public String getSslProvider() {
            return sslProvider;
        }

        public void setSslProvider(String sslProvider) {
            this.sslProvider = sslProvider;
        }

        public String getKeyStoreType() {
            return keyStoreType;
        }

        public void setKeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }

        public String getKeystore() {
            return keystore;
        }

        public void setKeystore(String keystore) {
            this.keystore = keystore;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public void setKeystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
        }

        public String getTruststore() {
            return truststore;
        }

        public void setTruststore(String truststore) {
            this.truststore = truststore;
        }

        public String getTruststorePassword() {
            return truststorePassword;
        }

        public void setTruststorePassword(String truststorePassword) {
            this.truststorePassword = truststorePassword;
        }

        public List<String> getProtocols() {
            return protocols;
        }

        public void setProtocols(List<String> protocols) {
            this.protocols = protocols;
        }

        public List<String> getCipherSuites() {
            return cipherSuites;
        }

        public void setCipherSuites(List<String> cipherSuites) {
            this.cipherSuites = cipherSuites;
        }

        public Long getHandshakeTimeout() {
            return handshakeTimeout;
        }

        public void setHandshakeTimeout(Long handshakeTimeout) {
            this.handshakeTimeout = handshakeTimeout;
        }
    }

}
