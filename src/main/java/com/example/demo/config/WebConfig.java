package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File uiDistDir = new File("ui/dist");
        if (!uiDistDir.exists()) {
            uiDistDir = new File(System.getProperty("user.dir"), "ui/dist");
        }

        String uiDistPath = uiDistDir.getAbsolutePath();

        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + uiDistPath + "/")
                .setCachePeriod(0);
    }
}
