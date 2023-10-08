package com.igeeksky.xcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-27
 */
@SpringBootApplication(scanBasePackages = "com.igeeksky.xcache")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
