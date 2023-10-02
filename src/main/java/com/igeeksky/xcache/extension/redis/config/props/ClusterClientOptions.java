package com.igeeksky.xcache.extension.redis.config.props;

import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;

import java.util.function.Predicate;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
public class ClusterClientOptions extends ClientOptions {

    private boolean closeStaleConnections;

    private int maxRedirects;

    private boolean validateClusterNodeMembership;

    private Predicate<RedisClusterNode> nodeFilter;

    private ClusterTopologyRefreshOptions topologyRefreshOptions;

    public boolean isCloseStaleConnections() {
        return closeStaleConnections;
    }

    public void setCloseStaleConnections(boolean closeStaleConnections) {
        this.closeStaleConnections = closeStaleConnections;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    public boolean isValidateClusterNodeMembership() {
        return validateClusterNodeMembership;
    }

    public void setValidateClusterNodeMembership(boolean validateClusterNodeMembership) {
        this.validateClusterNodeMembership = validateClusterNodeMembership;
    }

    public Predicate<RedisClusterNode> getNodeFilter() {
        return nodeFilter;
    }

    public void setNodeFilter(Predicate<RedisClusterNode> nodeFilter) {
        this.nodeFilter = nodeFilter;
    }

    public ClusterTopologyRefreshOptions getTopologyRefreshOptions() {
        return topologyRefreshOptions;
    }

    public void setTopologyRefreshOptions(ClusterTopologyRefreshOptions topologyRefreshOptions) {
        this.topologyRefreshOptions = topologyRefreshOptions;
    }

}
