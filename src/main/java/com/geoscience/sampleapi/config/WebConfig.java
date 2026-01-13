package com.geoscience.sampleapi.config;

import org.springframework.beans.factory.annotation.Value;
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
 * 
 * It properly handles base paths by stripping them before looking up
 * static resources. This allows the frontend to be built with a base
 * path (e.g., /geological-sample-api/) while Spring Boot serves files
 * from the root of the static resources directory.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.base-path:/geological-sample-api}")
    private String basePath;

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
                        // Strip the base path from the resource path if present
                        // Example: /geological-sample-api/assets/index.js -> /assets/index.js
                        String normalizedPath = resourcePath;
                        if (basePath != null && !basePath.isEmpty() && !basePath.equals("/")) {
                            // Remove leading slash from basePath for comparison
                            String basePathNormalized = basePath.startsWith("/") 
                                ? basePath.substring(1) 
                                : basePath;
                            // Remove trailing slash if present
                            if (basePathNormalized.endsWith("/")) {
                                basePathNormalized = basePathNormalized.substring(0, basePathNormalized.length() - 1);
                            }
                            
                            // Strip base path from resource path
                            if (normalizedPath.startsWith(basePathNormalized + "/")) {
                                normalizedPath = normalizedPath.substring(basePathNormalized.length());
                            } else if (normalizedPath.equals(basePathNormalized)) {
                                normalizedPath = "/";
                            }
                        }
                        
                        // Ensure path starts with / for proper resource lookup
                        if (!normalizedPath.startsWith("/")) {
                            normalizedPath = "/" + normalizedPath;
                        }
                        
                        Resource requestedResource = location.createRelative(normalizedPath);
                        
                        // If the requested resource exists, serve it
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // For API routes, don't serve index.html (let controllers handle them)
                        if (normalizedPath.startsWith("/api/")) {
                            return null;
                        }
                        
                        // For all other routes (SPA routing), serve index.html
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
