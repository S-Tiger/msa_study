package com.example.userservice;

import com.example.userservice.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class UserServcieApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServcieApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    @LoadBalanced
//    public RestTemplate getRestTemplate(){
//        return new RestTemplate();
//    }

    @Bean
    public Logger.Level feingLoggerLevel() {
        return Logger.Level.FULL;
    }

//    @Bean
//    public FeignErrorDecoder getFeignErrorDecoder() {
//        return new FeignErrorDecoder(); // 사용 구문에 따로 주입 안해줘도 된다
//    }

}
