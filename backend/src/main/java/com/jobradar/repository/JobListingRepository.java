package com.jobradar.repository;
import com.jobradar.domain.JobListing;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface JobListingRepository extends JpaRepository<JobListing,Long>,JpaSpecificationExecutor<JobListing>{
 Optional<JobListing> findBySourceAndExternalId(String source,String externalId);
}