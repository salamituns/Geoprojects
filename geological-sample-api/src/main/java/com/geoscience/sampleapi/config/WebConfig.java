package com.geoscience.sampleapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web configuration for serving static frontend files.
 * 
 * This configuration allows Spring Boot to serve the React frontend
 * as static files and handles client-side routing by serving index.html
 * for all non-API routes.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static files from /static/ directory
        // Spring Boot by default serves from classpath:/static/, but we configure it explicitly
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // If the requested resource exists, serve it
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // For API routes, don't serve index.html (let controllers handle them)
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        
                        // For all other routes (SPA routing), serve index.html
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
