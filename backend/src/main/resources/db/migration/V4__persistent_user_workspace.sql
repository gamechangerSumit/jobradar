CREATE TABLE favorite_jobs (
 id BIGSERIAL PRIMARY KEY,
 user_id BIGINT NOT NULL REFERENCES user_accounts(id) ON DELETE CASCADE,
 job_id BIGINT NOT NULL REFERENCES job_listings(id) ON DELETE CASCADE,
 saved_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
 CONSTRAINT uk_favorite_user_job UNIQUE(user_id,job_id)
);
CREATE INDEX idx_favorite_user_saved ON favorite_jobs(user_id,saved_at DESC);
CREATE TABLE job_applications (
 id BIGSERIAL PRIMARY KEY,
 user_id BIGINT NOT NULL REFERENCES user_accounts(id) ON DELETE CASCADE,
 job_id BIGINT NOT NULL REFERENCES job_listings(id) ON DELETE CASCADE,
 status VARCHAR(30) NOT NULL,
 notes VARCHAR(2000),
 updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
 CONSTRAINT uk_application_user_job UNIQUE(user_id,job_id),
 CONSTRAINT ck_application_status CHECK(status IN ('Interested','Applied','Interview','Offer','Rejected'))
);
CREATE INDEX idx_application_user_updated ON job_applications(user_id,updated_at DESC);
