package com.geoscience.sampleapi.repository;

import com.geoscience.sampleapi.model.GeologicalSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for geological sample data access operations.
 * 
 * Provides CRUD operations and custom query methods for sample management.
 */
@Repository
public interface SampleRepository extends JpaRepository<GeologicalSample, UUID> {

    /**
     * Find a sample by its unique identifier.
     * 
     * @param sampleIdentifier the unique sample identifier
     * @return Optional containing the sample if found
     */
    Optional<GeologicalSample> findBySampleIdentifier(String sampleIdentifier);

    /**
     * Check if a sample with the given identifier exists.
     * 
     * @param sampleIdentifier the unique sample identifier
     * @return true if exists, false otherwise
     */
    boolean existsBySampleIdentifier(String sampleIdentifier);
}

