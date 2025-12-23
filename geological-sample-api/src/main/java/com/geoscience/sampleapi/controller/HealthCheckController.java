package com.geoscience.sampleapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint for monitoring and load balancer health checks.
 * 
 * Provides a simple endpoint to verify the API is running and responsive.
 */
@RestController
public class HealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping("/healthcheck")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.debug("Health check endpoint called");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Geological Sample Management API");
        return ResponseEntity.ok(response);
    }
}

