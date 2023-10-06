package com.igeeksky.xcache.support.lettuce.config.props;

import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class ClusterClientOptions extends ClientOptions {

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

}