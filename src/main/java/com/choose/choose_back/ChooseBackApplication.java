package com.choose.choose_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class ChooseBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChooseBackApplication.class, args);
    }

}
