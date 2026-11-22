CREATE TABLE job_listings (
 id BIGSERIAL PRIMARY KEY,
 title VARCHAR(500) NOT NULL,
 company VARCHAR(300),
 location VARCHAR(1000),
 city VARCHAR(160),
 area VARCHAR(160),
 category VARCHAR(120),
 employment_type VARCHAR(80),
 experience_level VARCHAR(80),
 description VARCHAR(4000),
 apply_url VARCHAR(2000),
 source VARCHAR(100) NOT NULL,
 external_id VARCHAR(250) NOT NULL,
 latitude DOUBLE PRECISION,
 longitude DOUBLE PRECISION,
 posted_at TIMESTAMP WITH TIME ZONE,
 discovered_at TIMESTAMP WITH TIME ZONE NOT NULL,
 updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT uk_job_source_external UNIQUE(source,external_id)
);
CREATE INDEX idx_job_city ON job_listings(city);
CREATE INDEX idx_job_category ON job_listings(category);
CREATE INDEX idx_job_posted ON job_listings(posted_at DESC);
