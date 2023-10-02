package com.igeeksky.xcache.extension.redis.config.props;

import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class Cluster {

    private List<String> nodes;

    private Integer maxRedirects;

    private final Refresh refresh = new Refresh();

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public Integer getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(Integer maxRedirects) {
        System.out.println(maxRedirects);
        this.maxRedirects = maxRedirects;
    }

    public Refresh getRefresh() {
        return refresh;
    }

    public static class Refresh {

        /**
         * Whether to discover and query all cluster nodes for obtaining the
         * cluster topology. When set to false, only the initial seed nodes are
         * used as sources for topology discovery.
         */
        private boolean dynamicRefreshSources = true;

        /**
         * Cluster topology refresh period.
         */
        private Duration period;

        /**
         * Whether adaptive topology refreshing using all available refresh
         * triggers should be used.
         */
        private boolean adaptive;

        public boolean isDynamicRefreshSources() {
            return this.dynamicRefreshSources;
        }

        public void setDynamicRefreshSources(boolean dynamicRefreshSources) {
            this.dynamicRefreshSources = dynamicRefreshSources;
        }

        public Duration getPeriod() {
            return this.period;
        }

        public void setPeriod(Duration period) {
            this.period = period;
        }

        public boolean isAdaptive() {
            return this.adaptive;
        }

        public void setAdaptive(boolean adaptive) {
            this.adaptive = adaptive;
        }

    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("nodes='" + getNodes() + "'")
                .add("maxRedirects=" + maxRedirects)
                .toString();
    }
}
