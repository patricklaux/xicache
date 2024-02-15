package com.igeeksky.xcache;

import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-10-08
 */
@Service
public class TestService {

    //@CacheAutowired(name = "user", valueType = List.class, valueParams = {String.class})
    private Cache<String, List<String>> cache;

    // @CacheAutowired(name = "user", valueType = List.class, valueParams = {String.class})
    // private String name;

    @PostConstruct
    public void postConstruct() {
        Hooks.onOperatorDebug();

        // BeanFactoryCacheOperationSourceAdvisor

        // CacheInterceptor

        // cache.put("111", Mono.justOrEmpty(Arrays.asList("你好", "xcache"))).subscribe();

        // cache.get("111").subscribe(cv -> cv.getValue().forEach(System.out::println));

        // String strings = redisTemplate.opsForValue().get("user::111");
        // System.out.println("strings:----------------" + strings);

        Hooks.resetOnOperatorDebug();
    }

    @CachePut
    public Mono<String> getUserById(String id) {

        return Mono.empty();
    }

}