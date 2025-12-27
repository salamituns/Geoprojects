package com.geoscience.sampleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main application class for the Geological Sample Management API.
 * 
 * This REST API provides endpoints for managing geological samples collected
 * during field surveys, including rock samples, minerals, soil samples, and
 * other geological specimens.
 * 
 * Extends SpringBootServletInitializer to enable WAR deployment to Tomcat.
 */
@SpringBootApplication
public class GeologicalSampleApiApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GeologicalSampleApiApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(GeologicalSampleApiApplication.class, args);
    }
}

