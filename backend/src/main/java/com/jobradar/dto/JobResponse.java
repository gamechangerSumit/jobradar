package com.jobradar.dto;
import com.jobradar.domain.JobListing;
import java.time.OffsetDateTime;
public record JobResponse(Long id,String title,String company,String location,String city,String area,String category,String employmentType,String experienceLevel,String description,String applyUrl,String source,OffsetDateTime postedAt,OffsetDateTime discoveredAt,Double distanceKm){
 public static JobResponse from(JobListing j,Double d){return new JobResponse(j.getId(),j.getTitle(),j.getCompany(),j.getLocation(),j.getCity(),j.getArea(),j.getCategory(),j.getEmploymentType(),j.getExperienceLevel(),j.getDescription(),j.getApplyUrl(),j.getSource(),j.getPostedAt(),j.getDiscoveredAt(),d);}
}