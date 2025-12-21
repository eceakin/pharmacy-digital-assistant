package com.pharmacy.assistant.infrastructure.config; //aket adını kendi yapına göre düzenle

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Tüm API endpointleri için
                .allowedOrigins(allowedOrigins.split(",")) // YML'den gelen adreslere izin ver
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // İzin verilen metodlar
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}