package com.dodal.meet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DodalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DodalApplication.class, args);
    }

}
