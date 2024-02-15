package com.igeeksky.xcache.aop;

import com.igeeksky.xcache.annotation.EnableCache;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-13
 */
public class CacheConfigSelector extends AdviceModeImportSelector<EnableCache> {

    @Nullable
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return getProxy();
            case ASPECTJ:
            default:
                return null;
        }
    }

    private String[] getProxy() {
        List<String> result = new ArrayList<>();
        result.add(AutoProxyRegistrar.class.getName());
        result.add(ProxyCacheConfiguration.class.getName());
        return result.toArray(new String[0]);
    }

}
