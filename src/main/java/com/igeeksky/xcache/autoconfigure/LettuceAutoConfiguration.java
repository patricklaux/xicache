package com.igeeksky.xcache.autoconfigure;

import com.igeeksky.xcache.XcacheManager;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-18
 */
public class LettuceAutoConfiguration {

    private XcacheManager xcacheManager;


    /*
     TODO
        1. LettuceAutoConfiguration 获取配置文件
        2. LettuceConfiguration 生成多个 LettuceConnectionManager
        3. LettuceConfiguration 生成多个 LettuceCacheStoreProvider(LettuceConnectionManager)，添加到 multiLevelCacheManager
        4. LettuceConfiguration 生成多个 LettuceCacheSyncProvider (LettuceConnectionManager)，添加到 multiLevelCacheManager
    */


}
