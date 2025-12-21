package com.pharmacy.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PharmacyDigitalAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(PharmacyDigitalAssistantApplication.class, args);
    }

}
