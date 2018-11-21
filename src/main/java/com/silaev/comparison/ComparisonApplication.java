package com.silaev.comparison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableReactiveMongoRepositories
@EnableWebFlux
@SpringBootApplication
public class ComparisonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComparisonApplication.class, args);
    }
}
