package com.modak.modakapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ModakappApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModakappApplication.class, args);
        System.out.println("배포 테스트입니다.");
    }
}
