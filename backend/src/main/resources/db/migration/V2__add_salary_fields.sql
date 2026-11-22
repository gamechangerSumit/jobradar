ALTER TABLE job_listings ADD COLUMN salary_min DOUBLE PRECISION;
ALTER TABLE job_listings ADD COLUMN salary_max DOUBLE PRECISION;
ALTER TABLE job_listings ADD COLUMN salary_currency VARCHAR(8);
CREATE INDEX idx_job_salary_min ON job_listings(salary_min);
