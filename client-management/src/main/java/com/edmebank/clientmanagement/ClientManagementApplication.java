package com.edmebank.clientmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.edmebank.clientmanagement.client")
@EnableScheduling
public class ClientManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientManagementApplication.class, args);
    }

}
