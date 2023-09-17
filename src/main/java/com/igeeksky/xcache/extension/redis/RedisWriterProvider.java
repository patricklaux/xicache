package com.igeeksky.xcache.extension.redis;

import java.nio.charset.Charset;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-07-27
 */
public interface RedisWriterProvider {

    RedisStringWriter getRedisStringWriter(Charset charset);

    RedisHashWriter getRedisHashWriter(Charset charset);

}
