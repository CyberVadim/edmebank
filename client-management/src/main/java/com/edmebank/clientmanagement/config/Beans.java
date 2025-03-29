package com.edmebank.clientmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Beans {

    @Bean(destroyMethod = "shutdown")
    ExecutorService executorService() {
        int threads = 2 * Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(threads);
    }
}
