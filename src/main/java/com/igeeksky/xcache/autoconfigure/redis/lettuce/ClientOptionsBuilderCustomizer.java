package com.igeeksky.xcache.autoconfigure.redis.lettuce;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-05
 */
public interface ClientOptionsBuilderCustomizer {

    default TimeoutOptions.Builder customize(String id, TimeoutOptions.Builder builder) {
        return builder;
    }

    default SslOptions.Builder customize(String id, SslOptions.Builder builder) {
        return builder;
    }

    default ClientOptions.Builder customize(String id, ClientOptions.Builder builder) {
        return builder;
    }

    default ClientOptions.Builder customize(String id, ClusterClientOptions.Builder builder) {
        return builder;
    }

}
