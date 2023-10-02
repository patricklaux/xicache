package com.igeeksky.xcache.autoconfigure.lettuce;

import com.igeeksky.xcache.autoconfigure.Store;
import com.igeeksky.xcache.extension.redis.config.props.Lettuce;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
@Configuration
@ConfigurationProperties(prefix = "xcache.redis.lettuce")
public class LettuceProperties {

    private List<Store> stores;

    private List<Store> syncs;

    private List<Lettuce> connections;

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public List<Store> getSyncs() {
        return syncs;
    }

    public void setSyncs(List<Store> syncs) {
        this.syncs = syncs;
    }

    public List<Lettuce> getConnections() {
        return connections;
    }

    public void setConnections(List<Lettuce> connections) {
        this.connections = connections;
    }

}
