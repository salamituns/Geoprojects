package com.geoscience.sampleapi.service;

import com.geoscience.sampleapi.dto.SampleRequest;
import com.geoscience.sampleapi.dto.SampleResponse;
import com.geoscience.sampleapi.exception.SampleNotFoundException;
import com.geoscience.sampleapi.model.GeologicalSample;
import com.geoscience.sampleapi.repository.SampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer for geological sample business logic.
 * 
 * Handles sample CRUD operations, validation, and data transformation
 * between DTOs and entities.
 */
@Service
@Transactional
public class SampleService {

    private static final Logger logger = LoggerFactory.getLogger(SampleService.class);
    private final SampleRepository sampleRepository;

    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    /**
     * Create a new geological sample.
     * 
     * @param request the sample creation request
     * @return the created sample response
     */
    public SampleResponse createSample(SampleRequest request) {
        logger.info("Creating new sample with identifier: {}", request.getSampleIdentifier());
        
        // Check if sample identifier already exists
        if (sampleRepository.existsBySampleIdentifier(request.getSampleIdentifier())) {
            logger.warn("Sample identifier already exists: {}", request.getSampleIdentifier());
            throw new IllegalArgumentException("Sample identifier already exists: " + request.getSampleIdentifier());
        }

        GeologicalSample sample = GeologicalSample.builder()
                .sampleIdentifier(request.getSampleIdentifier())
                .sampleName(request.getSampleName())
                .sampleType(request.getSampleType())
                .collectionDate(request.getCollectionDate())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .locationName(request.getLocationName())
                .collectorName(request.getCollectorName())
                .description(request.getDescription())
                .storageLocation(request.getStorageLocation())
                .build();

        GeologicalSample savedSample = sampleRepository.save(sample);
        logger.info("Successfully created sample with id: {}", savedSample.getId());
        return mapToResponse(savedSample);
    }

    /**
     * Get all samples with pagination support.
     * 
     * @param pageable pagination parameters
     * @return page of sample responses
     */
    @Transactional(readOnly = true)
    public Page<SampleResponse> getAllSamples(Pageable pageable) {
        logger.debug("Fetching all samples with pagination: {}", pageable);
        return sampleRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get a sample by its ID.
     * 
     * @param id the sample ID
     * @return the sample response
     * @throws SampleNotFoundException if sample not found
     */
    @Transactional(readOnly = true)
    public SampleResponse getSampleById(UUID id) {
        logger.debug("Fetching sample with id: {}", id);
        GeologicalSample sample = sampleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Sample not found with id: {}", id);
                    return new SampleNotFoundException(id);
                });
        return mapToResponse(sample);
    }

    /**
     * Update an existing sample.
     * 
     * @param id the sample ID
     * @param request the update request
     * @return the updated sample response
     * @throws SampleNotFoundException if sample not found
     */
    public SampleResponse updateSample(UUID id, SampleRequest request) {
        logger.info("Updating sample with id: {}", id);
        
        GeologicalSample sample = sampleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Sample not found with id: {}", id);
                    return new SampleNotFoundException(id);
                });

        // Check if new identifier conflicts with existing sample (excluding current)
        if (!sample.getSampleIdentifier().equals(request.getSampleIdentifier())) {
            if (sampleRepository.existsBySampleIdentifier(request.getSampleIdentifier())) {
                logger.warn("Sample identifier already exists: {}", request.getSampleIdentifier());
                throw new IllegalArgumentException("Sample identifier already exists: " + request.getSampleIdentifier());
            }
        }

        // Update sample fields
        sample.setSampleIdentifier(request.getSampleIdentifier());
        sample.setSampleName(request.getSampleName());
        sample.setSampleType(request.getSampleType());
        sample.setCollectionDate(request.getCollectionDate());
        sample.setLatitude(request.getLatitude());
        sample.setLongitude(request.getLongitude());
        sample.setLocationName(request.getLocationName());
        sample.setCollectorName(request.getCollectorName());
        sample.setDescription(request.getDescription());
        sample.setStorageLocation(request.getStorageLocation());

        GeologicalSample updatedSample = sampleRepository.save(sample);
        logger.info("Successfully updated sample with id: {}", updatedSample.getId());
        return mapToResponse(updatedSample);
    }

    /**
     * Delete a sample by its ID.
     * 
     * @param id the sample ID
     * @throws SampleNotFoundException if sample not found
     */
    public void deleteSample(UUID id) {
        logger.info("Deleting sample with id: {}", id);
        
        if (!sampleRepository.existsById(id)) {
            logger.warn("Sample not found with id: {}", id);
            throw new SampleNotFoundException(id);
        }
        
        sampleRepository.deleteById(id);
        logger.info("Successfully deleted sample with id: {}", id);
    }

    /**
     * Map entity to response DTO.
     */
    private SampleResponse mapToResponse(GeologicalSample sample) {
        return SampleResponse.builder()
                .id(sample.getId())
                .sampleIdentifier(sample.getSampleIdentifier())
                .sampleName(sample.getSampleName())
                .sampleType(sample.getSampleType())
                .collectionDate(sample.getCollectionDate())
                .latitude(sample.getLatitude())
                .longitude(sample.getLongitude())
                .locationName(sample.getLocationName())
                .collectorName(sample.getCollectorName())
                .description(sample.getDescription())
                .storageLocation(sample.getStorageLocation())
                .createdAt(sample.getCreatedAt())
                .updatedAt(sample.getUpdatedAt())
                .build();
    }
}

