package com.igeeksky.xcache.support.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igeeksky.xcache.extension.serializer.AbstractSerializerProvider;
import com.igeeksky.xcache.extension.serializer.Serializer;

import java.nio.charset.Charset;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-22
 */
public class JacksonSerializerProvider extends AbstractSerializerProvider {

    @Override
    public <T> Serializer<T> doGet(Class<T> type, Charset charset) {
        return new JacksonSerializer<>(new ObjectMapper(), type, charset);
    }

}
