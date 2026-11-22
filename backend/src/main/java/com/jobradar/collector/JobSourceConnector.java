package com.jobradar.collector;
import com.jobradar.domain.JobListing;
import java.util.List;
/**
 * Implement this interface for a job source whose API/feed terms permit collection.
 * Return stable external IDs and normalized location/category values.
 */
public interface JobSourceConnector {
 String sourceName();
 List<JobListing> fetchJobs();
}