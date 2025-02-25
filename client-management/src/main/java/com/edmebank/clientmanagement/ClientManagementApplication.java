package com.edmebank.clientmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.edmebank.clientmanagement.client")
public class ClientManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientManagementApplication.class, args);
    }

}
