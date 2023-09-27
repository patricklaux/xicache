package com.igeeksky.xcache.extension.redis;

/**
 * Redis命令接口
 *
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-03
 */
public interface RedisWriter {

    String OK = "OK";

    boolean isCluster();

}
