package com.jobradar.dto;

import com.jobradar.domain.JobListing;
import java.time.OffsetDateTime;

public record JobResponse(
        Long id, String title, String company, String location, String city, String area,
        String category, String employmentType, String experienceLevel, String description,
        String applyUrl, String source, OffsetDateTime postedAt, OffsetDateTime discoveredAt,
        Double distanceKm, Double salaryMin, Double salaryMax, String salaryCurrency) {
    public static JobResponse from(JobListing job, Double distance) {
        return new JobResponse(job.getId(), job.getTitle(), job.getCompany(), job.getLocation(),
                job.getCity(), job.getArea(), job.getCategory(), job.getEmploymentType(),
                job.getExperienceLevel(), job.getDescription(), job.getApplyUrl(), job.getSource(),
                job.getPostedAt(), job.getDiscoveredAt(), distance, job.getSalaryMin(),
                job.getSalaryMax(), job.getSalaryCurrency());
    }
}