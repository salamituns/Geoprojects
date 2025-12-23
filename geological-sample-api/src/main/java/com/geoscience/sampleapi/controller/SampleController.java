package com.geoscience.sampleapi.controller;

import com.geoscience.sampleapi.dto.SampleRequest;
import com.geoscience.sampleapi.dto.SampleResponse;
import com.geoscience.sampleapi.service.SampleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for geological sample management endpoints.
 * 
 * Provides CRUD operations for managing geological samples with proper
 * HTTP verbs, status codes, and API versioning.
 */
@RestController
@RequestMapping("/api/v1/samples")
public class SampleController {

    private static final Logger logger = LoggerFactory.getLogger(SampleController.class);
    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    /**
     * Create a new geological sample.
     * 
     * @param request the sample creation request
     * @return the created sample with 201 status
     */
    @PostMapping
    public ResponseEntity<SampleResponse> createSample(@Valid @RequestBody SampleRequest request) {
        logger.info("POST /api/v1/samples - Creating new sample: {}", request.getSampleIdentifier());
        SampleResponse response = sampleService.createSample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all geological samples with pagination.
     * 
     * @param pageable pagination parameters (page, size, sort)
     * @return page of samples with 200 status
     */
    @GetMapping
    public ResponseEntity<Page<SampleResponse>> getAllSamples(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        logger.info("GET /api/v1/samples - Fetching all samples with pagination: {}", pageable);
        Page<SampleResponse> samples = sampleService.getAllSamples(pageable);
        return ResponseEntity.ok(samples);
    }

    /**
     * Get a geological sample by ID.
     * 
     * @param id the sample ID
     * @return the sample with 200 status, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<SampleResponse> getSampleById(@PathVariable UUID id) {
        logger.info("GET /api/v1/samples/{} - Fetching sample by id", id);
        SampleResponse response = sampleService.getSampleById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing geological sample.
     * 
     * @param id the sample ID
     * @param request the update request
     * @return the updated sample with 200 status, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<SampleResponse> updateSample(
            @PathVariable UUID id,
            @Valid @RequestBody SampleRequest request) {
        logger.info("PUT /api/v1/samples/{} - Updating sample", id);
        SampleResponse response = sampleService.updateSample(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a geological sample by ID.
     * 
     * @param id the sample ID
     * @return 204 No Content on success, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSample(@PathVariable UUID id) {
        logger.info("DELETE /api/v1/samples/{} - Deleting sample", id);
        sampleService.deleteSample(id);
        return ResponseEntity.noContent().build();
    }
}

