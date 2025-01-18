package com.moneybook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneybookApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneybookApplication.class, args);
    }

}
