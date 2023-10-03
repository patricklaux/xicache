package com.igeeksky.xcache.autoconfigure.redis;

import com.igeeksky.xcache.autoconfigure.Store;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-02
 */
@Configuration
@ConfigurationProperties(prefix = "xcache.redis")
public class RedisProperties {

    private List<Store> stores;

    private List<Store> syncs;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("\"stores\":" + stores)
                .add("\"syncs\":" + syncs)
                .toString();
    }
}
