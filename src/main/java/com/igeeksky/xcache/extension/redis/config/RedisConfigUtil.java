package com.igeeksky.xcache.extension.redis.config;

import com.igeeksky.xcache.extension.redis.RedisNode;
import com.igeeksky.xtool.core.lang.ArrayUtils;
import com.igeeksky.xtool.core.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-26
 */
public class RedisConfigUtil {

    public static List<RedisNode> getRedisNodes(List<String> nodes) {
        // TODO 校验
        List<RedisNode> redisNodes = new ArrayList<>(nodes.size());
        for (String node : nodes) {
            node = StringUtils.trim(node);
            if (!StringUtils.hasLength(node)) {
                continue;
            }
            String[] hostAndPort = node.split(":");
            if (ArrayUtils.isEmpty(hostAndPort)) {
                continue;
            }
            String host = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            redisNodes.add(new RedisNode(host, port));
        }
        return redisNodes;
    }

}
