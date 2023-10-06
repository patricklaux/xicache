package com.igeeksky.xcache.support.lettuce.config.props;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class Cluster extends Generic {

    private String readFrom;
    private List<String> nodes;
    private ClusterClientOptions clientOptions;

    public String getReadFrom() {
        return readFrom;
    }

    public void setReadFrom(String readFrom) {
        this.readFrom = readFrom;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public ClusterClientOptions getClientOptions() {
        return clientOptions;
    }

    public void setClientOptions(ClusterClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }
}