package com.sutusxxx.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {
        "com.sutusxxx.auth",
        "com.sutusxxx.user"
})
@ComponentScan(basePackages = {
        "com.sutusxxx.auth",
        "com.sutusxxx.commons",
        "com.sutusxxx.user"
})
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
