package com.geoscience.sampleapi.dto;

import com.geoscience.sampleapi.model.GeologicalSample;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for returning geological sample data in API responses.
 * 
 * Provides a clean API contract and allows for future extensions without
 * exposing internal entity structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleResponse {

    private UUID id;
    private String sampleIdentifier;
    private String sampleName;
    private GeologicalSample.SampleType sampleType;
    private LocalDate collectionDate;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private String collectorName;
    private String description;
    private String storageLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

