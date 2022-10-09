package com.example.springapp;

import org.outofoffice.eidaspringsupport.EnableEida;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication @EnableEida
public class SpringAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAppApplication.class, args);
    }
}
