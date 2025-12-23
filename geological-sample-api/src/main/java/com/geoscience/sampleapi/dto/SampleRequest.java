package com.geoscience.sampleapi.dto;

import com.geoscience.sampleapi.model.GeologicalSample;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating a geological sample.
 * 
 * Used to receive sample data from API requests, separating the API contract
 * from the internal entity model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleRequest {

    @NotBlank(message = "Sample identifier is required")
    private String sampleIdentifier;

    @NotBlank(message = "Sample name is required")
    private String sampleName;

    @NotNull(message = "Sample type is required")
    private GeologicalSample.SampleType sampleType;

    @NotNull(message = "Collection date is required")
    private LocalDate collectionDate;

    private Double latitude;

    private Double longitude;

    private String locationName;

    @NotBlank(message = "Collector name is required")
    private String collectorName;

    private String description;

    private String storageLocation;
}

