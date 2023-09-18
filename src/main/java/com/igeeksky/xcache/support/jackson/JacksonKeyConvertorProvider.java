package com.igeeksky.xcache.support.jackson;

import com.igeeksky.xcache.extension.convertor.KeyConvertor;
import com.igeeksky.xcache.extension.convertor.KeyConvertorProvider;

import java.nio.charset.Charset;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
public class JacksonKeyConvertorProvider implements KeyConvertorProvider {

    @Override
    public KeyConvertor get(Charset charset) {
        return JacksonKeyConvertor.getInstance();
    }

}
