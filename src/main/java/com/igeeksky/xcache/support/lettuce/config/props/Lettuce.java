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
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class Lettuce {

    private String id;

    private String charset;

    private LettuceCluster cluster;

    private LettuceSentinel sentinel;

    private LettuceStandalone standalone;

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

    public LettuceCluster getCluster() {
        return cluster;
    }

    public void setCluster(LettuceCluster cluster) {
        this.cluster = cluster;
    }

    public LettuceSentinel getSentinel() {
        return sentinel;
    }

    public void setSentinel(LettuceSentinel sentinel) {
        this.sentinel = sentinel;
    }

    public LettuceStandalone getStandalone() {
        return standalone;
    }

    public void setStandalone(LettuceStandalone standalone) {
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

    private static void setGeneric(LettuceGeneric original, LettuceGenericConfig config) {
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
            sources.forEach(node -> nodes.add(new HostAndPort(node)));
        }
        return nodes;
    }


    public static class ClientOptions {

        private Boolean autoReconnect;

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

    }


    public static class ClusterClientOptions extends ClientOptions {

        private Integer maxRedirects;

        private Boolean validateClusterNodeMembership;

        // 白名单
        private Set<String> nodeFilter;

        private ClusterTopologyRefreshOptions topologyRefreshOptions;

        public Integer getMaxRedirects() {
            return maxRedirects;
        }

        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }

        public Boolean getValidateClusterNodeMembership() {
            return validateClusterNodeMembership;
        }

        public void setValidateClusterNodeMembership(Boolean validateClusterNodeMembership) {
            this.validateClusterNodeMembership = validateClusterNodeMembership;
        }

        public Set<String> getNodeFilter() {
            return nodeFilter;
        }

        public void setNodeFilter(Set<String> nodeFilter) {
            this.nodeFilter = nodeFilter;
        }

        public ClusterTopologyRefreshOptions getTopologyRefreshOptions() {
            return topologyRefreshOptions;
        }

        public void setTopologyRefreshOptions(ClusterTopologyRefreshOptions topologyRefreshOptions) {
            this.topologyRefreshOptions = topologyRefreshOptions;
        }

    }


    public static class ClusterTopologyRefreshOptions {

        private Set<String> adaptiveRefreshTriggers;

        private Long adaptiveRefreshTimeout;

        private Boolean closeStaleConnections;

        private Boolean dynamicRefreshSources;

        private Boolean periodicRefreshEnabled;

        private Long refreshPeriod;

        private Integer refreshTriggersReconnectAttempts;

        public Set<String> getAdaptiveRefreshTriggers() {
            return adaptiveRefreshTriggers;
        }

        public void setAdaptiveRefreshTriggers(Set<String> adaptiveRefreshTriggers) {
            this.adaptiveRefreshTriggers = adaptiveRefreshTriggers;
        }

        public Long getAdaptiveRefreshTimeout() {
            return adaptiveRefreshTimeout;
        }

        public void setAdaptiveRefreshTimeout(Long adaptiveRefreshTimeout) {
            this.adaptiveRefreshTimeout = adaptiveRefreshTimeout;
        }

        public Boolean getCloseStaleConnections() {
            return closeStaleConnections;
        }

        public void setCloseStaleConnections(Boolean closeStaleConnections) {
            this.closeStaleConnections = closeStaleConnections;
        }

        public Boolean getDynamicRefreshSources() {
            return dynamicRefreshSources;
        }

        public void setDynamicRefreshSources(Boolean dynamicRefreshSources) {
            this.dynamicRefreshSources = dynamicRefreshSources;
        }

        public Boolean getPeriodicRefreshEnabled() {
            return periodicRefreshEnabled;
        }

        public void setPeriodicRefreshEnabled(Boolean periodicRefreshEnabled) {
            this.periodicRefreshEnabled = periodicRefreshEnabled;
        }

        public Long getRefreshPeriod() {
            return refreshPeriod;
        }

        public void setRefreshPeriod(Long refreshPeriod) {
            this.refreshPeriod = refreshPeriod;
        }

        public Integer getRefreshTriggersReconnectAttempts() {
            return refreshTriggersReconnectAttempts;
        }

        public void setRefreshTriggersReconnectAttempts(Integer refreshTriggersReconnectAttempts) {
            this.refreshTriggersReconnectAttempts = refreshTriggersReconnectAttempts;
        }

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


    public static class TimeoutOptions {

        private Boolean timeoutCommands;

        private Long fixedTimeout;

        // 不同的命令采用不同的超时配置，需编程实现
        // private TimeoutSource timeoutSource

        public Boolean getTimeoutCommands() {
            return timeoutCommands;
        }

        public void setTimeoutCommands(Boolean timeoutCommands) {
            this.timeoutCommands = timeoutCommands;
        }

        public Long getFixedTimeout() {
            return fixedTimeout;
        }

        public void setFixedTimeout(Long fixedTimeout) {
            this.fixedTimeout = fixedTimeout;
        }

    }


    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("\"id\":\"" + id + "\"")
                .add("\"charset\":\"" + charset + "\"")
                .add("\"cluster\":" + cluster)
                .add("\"sentinel\":" + sentinel)
                .add("\"standalone\":" + standalone)
                .toString();
    }

}