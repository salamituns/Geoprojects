-- Migration script to create the samples table for geological sample management
-- This table stores information about geological samples collected during field surveys

CREATE TABLE IF NOT EXISTS samples (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sample_identifier VARCHAR(50) NOT NULL UNIQUE,
    sample_name VARCHAR(200) NOT NULL,
    sample_type VARCHAR(20) NOT NULL,
    collection_date DATE NOT NULL,
    latitude REAL,
    longitude REAL,
    location_name VARCHAR(200),
    collector_name VARCHAR(100) NOT NULL,
    description TEXT,
    storage_location VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for commonly queried fields
CREATE INDEX IF NOT EXISTS idx_sample_identifier ON samples(sample_identifier);
CREATE INDEX IF NOT EXISTS idx_collection_date ON samples(collection_date);

