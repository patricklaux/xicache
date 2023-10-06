package com.igeeksky.xcache.support.lettuce.config;

import com.igeeksky.xcache.config.HostAndPort;
import com.igeeksky.xcache.support.lettuce.config.props.ClusterClientOptions;
import io.lettuce.core.ReadFrom;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class LettuceClusterConfig extends LettuceGenericConfig {

    private ReadFrom readFrom;

    private List<HostAndPort> nodes;

    private ClusterClientOptions clientOptions;

    public ReadFrom getReadFrom() {
        return readFrom;
    }

    public void setReadFrom(ReadFrom readFrom) {
        this.readFrom = readFrom;
    }

    public List<HostAndPort> getNodes() {
        return nodes;
    }

    public void setNodes(List<HostAndPort> nodes) {
        this.nodes = nodes;
    }

    public ClusterClientOptions getClientOptions() {
        return clientOptions;
    }

    public void setClientOptions(ClusterClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

}