package com.learning.springbootwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SpringBootWebAppApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebAppApplication.class, args);
    }
}
