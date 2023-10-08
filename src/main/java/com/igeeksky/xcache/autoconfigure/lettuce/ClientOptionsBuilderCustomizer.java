package com.igeeksky.xcache.autoconfigure.lettuce;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-05
 */
public interface ClientOptionsBuilderCustomizer {

    default void customizeTimeout(String id, TimeoutOptions.Builder builder) {
    }

    default void customizeSsl(String id, SslOptions.Builder builder) {
    }

    default void customizeClient(String id, ClientOptions.Builder builder) {
    }

    default void customizeClusterClient(String id, ClusterClientOptions.Builder builder) {
    }

}
