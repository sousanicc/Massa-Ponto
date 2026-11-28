package com.massapontogrupo.massaponto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path pastaUploads;

    public WebConfig(@Value("${app.uploads.dir:uploads/produtos}") String uploadsDir) {
        this.pastaUploads = Paths.get(uploadsDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapear /uploads/** para a pasta física de uploads
        String localPath = this.pastaUploads.toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(localPath)
                .setCachePeriod(3600); // cache 1 hora
    }
}