package com.igeeksky.xcache.test;

import com.igeeksky.xcache.User;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-09
 */
public class GeneralTest {

    @Test
    void testThrowException() {
        Set<String> set = new HashSet<>();
        set.add(null);
        Mono.just(set)
                .doOnNext(ks -> {
                    System.out.println(ks.size());
                    ks.forEach(key -> {
                        if (null == key) {
                            throw new RuntimeException("error");
                        }
                    });
                })
                .doOnError(throwable -> System.out.printf("1[%s]\n", throwable))
                .doOnSuccess(ks -> System.out.println("success"))
                .doOnNext(ks -> System.out.println("doOnNext[" + ks + "]"))
                .subscribe(ks -> System.out.println("subscribe[" + ks + "]"));
    }

    @Test
    void testDoOnNext1() {
        hasValueReturn(Mono.just(new User("ssss"))).subscribe();
    }

    static Mono<Void> hasValueReturn(Mono<User> mono) {
        return mono.doOnNext(user -> user.setName("xxxx")).doOnNext(System.out::println).then();
    }

    @Test
    void testDoOnNext3() {
        emptyReturn1()
                .doOnSuccess(user -> System.out.println("执行1" + user))
                .map(user -> {
                    System.out.println("执行2" + user);
                    return user;
                })
                .switchIfEmpty(emptyReturn2())
                .doOnSuccess(user -> System.out.println("执行3" + user))
                .subscribe();
    }

    static Mono<User> emptyReturn1() {
        System.out.println("emptyReturn1");
        return Mono.fromSupplier(() -> null);
    }

    @Test
    void testDoOnNext4() {
        emptyReturn2().doOnSuccess(user -> System.out.println("执行2" + user)).subscribe();
    }

    static Mono<User> emptyReturn2() {
        System.out.println("emptyReturn2");
        return Mono.empty();
    }


}
