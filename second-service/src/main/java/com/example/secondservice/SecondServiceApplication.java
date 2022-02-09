package com.example.secondservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SecondServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondServiceApplication.class, args);
    }

}
