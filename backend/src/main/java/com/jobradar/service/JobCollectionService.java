package com.jobradar.service;

import com.jobradar.collector.JobSourceConnector;
import com.jobradar.domain.JobListing;
import com.jobradar.repository.JobListingRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobCollectionService {
    private static final Logger log = LoggerFactory.getLogger(JobCollectionService.class);
    private final List<JobSourceConnector> connectors;
    private final JobListingRepository repository;

    public JobCollectionService(List<JobSourceConnector> connectors, JobListingRepository repository) {
        this.connectors = connectors;
        this.repository = repository;
    }

    @Transactional
    public int collect() { return collect(null); }

    @Transactional
    public int collect(String city) {
        int processed = 0;
        for (JobSourceConnector connector : connectors) {
            try {
                List<JobListing> incomingJobs = city == null || city.isBlank()
                        ? connector.fetchJobs() : connector.fetchJobs(city.trim());
                for (JobListing incoming : incomingJobs) {
                    if (incoming.getExternalId() == null || incoming.getExternalId().isBlank()
                            || incoming.getTitle() == null || incoming.getTitle().isBlank()) {
                        log.warn("Skipping invalid record from {}", connector.sourceName());
                        continue;
                    }
                    var existing = repository.findBySourceAndExternalId(
                            connector.sourceName(), incoming.getExternalId());
                    if (existing.isPresent()) {
                        JobListing current = existing.get();
                        current.setTitle(incoming.getTitle());
                        current.setCompany(incoming.getCompany());
                        current.setLocation(incoming.getLocation());
                        current.setCity(incoming.getCity());
                        current.setArea(incoming.getArea());
                        current.setCategory(incoming.getCategory());
                        current.setEmploymentType(incoming.getEmploymentType());
                        current.setExperienceLevel(incoming.getExperienceLevel());
                        current.setDescription(incoming.getDescription());
                        current.setApplyUrl(incoming.getApplyUrl());
                        current.setLatitude(incoming.getLatitude());
                        current.setLongitude(incoming.getLongitude());
                        current.setSalaryMin(incoming.getSalaryMin());
                        current.setSalaryMax(incoming.getSalaryMax());
                        current.setSalaryCurrency(incoming.getSalaryCurrency());
                        current.setPostedAt(incoming.getPostedAt());
                    } else {
                        incoming.setSource(connector.sourceName());
                        repository.save(incoming);
                    }
                    processed++;
                }
            } catch (Exception ex) {
                log.error("Job source '{}' failed: {}", connector.sourceName(), ex.getMessage());
            }
        }
        if (connectors.isEmpty()) log.info("No job providers configured.");
        return processed;
    }

    public int configuredSourceCount() { return connectors.size(); }
}