package com.jxbg.ticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${ticket.upload.review-image-path:uploads/reviews}")
    private String reviewImagePath;

    @Value("${ticket.upload.review-image-url-prefix:/review-images}")
    private String reviewImageUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(reviewImagePath).toAbsolutePath().normalize().toUri().toString();
        String pattern = reviewImageUrlPrefix.endsWith("/")
                ? reviewImageUrlPrefix + "**"
                : reviewImageUrlPrefix + "/**";
        registry.addResourceHandler(pattern).addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
