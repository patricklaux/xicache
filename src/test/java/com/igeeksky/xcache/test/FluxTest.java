package com.igeeksky.xcache.test;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-17
 */
public class FluxTest {

    @Test
    void doFinally() {
        Flux.just(1, 2, 3)
                .doFinally(s -> System.out.println("doFinally:" + s))
                .doOnNext(n -> System.out.println("doOnNext:" + n))
                .subscribe(n -> System.out.println("subscribe:" + n));
    }

    @Test
    void doFinally2() {
        Flux.just(1, 2, 3)
                .doOnNext(n -> System.out.println("doOnNext:" + n))
                .then()
                .doFinally(s -> System.out.println("doFinally:" + s))
                .then()
                .doOnSuccess(n -> System.out.println("doOnNext2:" + n))
                .subscribe(n -> System.out.println("subscribe:" + n));
    }

}
