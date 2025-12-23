package com.geoscience.sampleapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Root controller that serves the frontend application.
 * 
 * When the frontend is built and placed in src/main/resources/static,
 * this controller serves the index.html file. If the frontend is not
 * available, it returns API information instead.
 */
@RestController
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    @GetMapping("/")
    public ResponseEntity<?> root() {
        logger.debug("Root endpoint accessed");
        
        // Try to serve the frontend index.html
        try {
            Resource indexHtml = new ClassPathResource("/static/index.html");
            if (indexHtml.exists() && indexHtml.isReadable()) {
                logger.debug("Serving frontend index.html");
                try {
                    String content = new String(indexHtml.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.TEXT_HTML);
                    return new ResponseEntity<>(content, headers, HttpStatus.OK);
                } catch (IOException e) {
                    logger.warn("Error reading index.html: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.debug("Frontend not found, serving API info instead: {}", e.getMessage());
        }
        
        // Fallback: return API information if frontend is not available
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Geological Sample Management API");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "UP");
        response.put("endpoints", Map.of(
            "health", "/healthcheck",
            "samples", "/api/v1/samples",
            "apiDocs", "See README.md for API documentation"
        ));
        response.put("note", "Frontend not built. Run 'make build' to build and include the frontend.");
        return ResponseEntity.ok(response);
    }
}
