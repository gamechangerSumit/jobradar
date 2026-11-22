package com.jobradar.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "job_listings", uniqueConstraints = @UniqueConstraint(
        name = "uk_job_source_external", columnNames = {"source", "external_id"}))
public class JobListing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 500) private String title;
    @Column(length = 300) private String company;
    @Column(length = 1000) private String location;
    @Column(length = 160) private String city;
    @Column(length = 160) private String area;
    @Column(length = 120) private String category;
    @Column(length = 80) private String employmentType;
    @Column(length = 80) private String experienceLevel;
    @Column(length = 4000) private String description;
    @Column(length = 2000) private String applyUrl;
    @Column(nullable = false, length = 100) private String source;
    @Column(name = "external_id", nullable = false, length = 250) private String externalId;
    private Double latitude;
    private Double longitude;
    private Double salaryMin;
    private Double salaryMax;
    @Column(length = 8) private String salaryCurrency;
    private OffsetDateTime postedAt;
    @Column(nullable = false) private OffsetDateTime discoveredAt;
    @Column(nullable = false) private OffsetDateTime updatedAt;

    @PrePersist void create() { var now = OffsetDateTime.now(); if (discoveredAt == null) discoveredAt = now; updatedAt = now; }
    @PreUpdate void update() { updatedAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public String getCompany() { return company; } public void setCompany(String v) { company = v; }
    public String getLocation() { return location; } public void setLocation(String v) { location = v; }
    public String getCity() { return city; } public void setCity(String v) { city = v; }
    public String getArea() { return area; } public void setArea(String v) { area = v; }
    public String getCategory() { return category; } public void setCategory(String v) { category = v; }
    public String getEmploymentType() { return employmentType; } public void setEmploymentType(String v) { employmentType = v; }
    public String getExperienceLevel() { return experienceLevel; } public void setExperienceLevel(String v) { experienceLevel = v; }
    public String getDescription() { return description; } public void setDescription(String v) { description = v; }
    public String getApplyUrl() { return applyUrl; } public void setApplyUrl(String v) { applyUrl = v; }
    public String getSource() { return source; } public void setSource(String v) { source = v; }
    public String getExternalId() { return externalId; } public void setExternalId(String v) { externalId = v; }
    public Double getLatitude() { return latitude; } public void setLatitude(Double v) { latitude = v; }
    public Double getLongitude() { return longitude; } public void setLongitude(Double v) { longitude = v; }
    public Double getSalaryMin() { return salaryMin; } public void setSalaryMin(Double v) { salaryMin = v; }
    public Double getSalaryMax() { return salaryMax; } public void setSalaryMax(Double v) { salaryMax = v; }
    public String getSalaryCurrency() { return salaryCurrency; } public void setSalaryCurrency(String v) { salaryCurrency = v; }
    public OffsetDateTime getPostedAt() { return postedAt; } public void setPostedAt(OffsetDateTime v) { postedAt = v; }
    public OffsetDateTime getDiscoveredAt() { return discoveredAt; }
}