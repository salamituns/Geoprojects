package com.geoscience.sampleapi.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested geological sample is not found.
 */
public class SampleNotFoundException extends RuntimeException {

    public SampleNotFoundException(String message) {
        super(message);
    }

    public SampleNotFoundException(UUID id) {
        super("Sample not found with id: " + id);
    }
}

