package com.geoscience.sampleapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a geological sample collected during field surveys.
 * 
 * Samples can include rocks, minerals, soil, fossils, sediments, and other
 * geological specimens with associated metadata such as location, collection
 * date, and storage information.
 */
@Entity
@Table(name = "samples")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeologicalSample {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "sample_identifier", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Sample identifier is required")
    private String sampleIdentifier;

    @Column(name = "sample_name", nullable = false, length = 200)
    @NotBlank(message = "Sample name is required")
    private String sampleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sample_type", nullable = false, length = 20)
    @NotNull(message = "Sample type is required")
    private SampleType sampleType;

    @Column(name = "collection_date", nullable = false)
    @NotNull(message = "Collection date is required")
    private LocalDate collectionDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "collector_name", nullable = false, length = 100)
    @NotBlank(message = "Collector name is required")
    private String collectorName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "storage_location", length = 200)
    private String storageLocation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enumeration of geological sample types.
     */
    public enum SampleType {
        ROCK,
        MINERAL,
        SOIL,
        FOSSIL,
        SEDIMENT,
        OTHER
    }
}

