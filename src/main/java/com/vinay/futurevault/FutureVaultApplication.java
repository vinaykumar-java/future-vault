package com.vinay.futurevault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FutureVaultApplication {


    public static void main(String[] args) {
        SpringApplication.run(FutureVaultApplication.class, args);
    }
}