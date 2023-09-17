package com.igeeksky.xcache.extension.serializer;

import com.igeeksky.xcache.config.CacheConfigException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Patrick.Lau
 * @since 0.0.3 2021-06-20
 */
public class StringSerializer implements Serializer<String> {

    public static final StringSerializer UTF_8 = new StringSerializer();

    public static final StringSerializer US_ASCII = new StringSerializer(StandardCharsets.US_ASCII);

    private static final ConcurrentMap<Charset, StringSerializer> STRING_SERIALIZER_MAP = new ConcurrentHashMap<>();

    private final Charset charset;

    public StringSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public StringSerializer(Charset charset) {
        this.charset = charset;
    }

    @Override
    public byte[] serialize(String str) {
        if (null == str) {
            throw new SerializationFailedException("str must not be null");
        }
        return str.getBytes(charset);
    }

    public static StringSerializer getInstance(Charset charset) {
        if (Objects.equals(StandardCharsets.UTF_8, charset)) {
            return UTF_8;
        }
        if (Objects.equals(StandardCharsets.US_ASCII, charset)) {
            return US_ASCII;
        }
        return STRING_SERIALIZER_MAP.computeIfAbsent(charset, key -> new StringSerializer(charset));
    }

    @Override
    public String deserialize(byte[] source) {
        if (null == source) {
            throw new SerializationFailedException("byte[] source must not be null");
        }
        return new String(source, charset);
    }

    public static class StringSerializerProvider implements SerializerProvider {

        private static final StringSerializerProvider INSTANCE = new StringSerializerProvider();

        public static StringSerializerProvider getInstance() {
            return INSTANCE;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Serializer<T> get(String name, Charset charset, Class<T> type) {
            if (Objects.equals(String.class, type)) {
                return (Serializer<T>) StringSerializer.getInstance(charset);
            }
            throw new CacheConfigException("type must be String.class. " + type);
        }

    }
}
