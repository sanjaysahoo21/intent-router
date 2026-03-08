package com.example.IntentRouter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Restrict CORS to trusted origins only.
     * Replace the allowedOrigins value with your actual front-end domain(s) before
     * deploying to production. Using "*" is intentionally avoided to prevent
     * cross-origin abuse.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // update for production
                .allowedMethods("POST")
                .allowedHeaders("Content-Type")
                .maxAge(3600);
    }
}
