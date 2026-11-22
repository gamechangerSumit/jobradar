package com.jobradar.service;

import com.jobradar.domain.JobListing;
import com.jobradar.dto.JobResponse;
import com.jobradar.repository.JobListingRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class JobService {
    private final JobListingRepository repository;

    public JobService(JobListingRepository repository) { this.repository = repository; }

    public List<JobResponse> search(String keyword, String city, String category, String type,
            String experience, Integer hours, Double latitude, Double longitude,
            Double radiusKm, int limit) {
        Specification<JobListing> spec = (root, query, cb) -> cb.conjunction();
        if (has(keyword)) {
            String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((r, q, cb) -> cb.or(
                    cb.like(cb.lower(r.get("title")), pattern),
                    cb.like(cb.lower(r.get("company")), pattern),
                    cb.like(cb.lower(r.get("description")), pattern)));
        }
        if (has(city)) {
            String pattern = "%" + city.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((r, q, cb) -> cb.or(
                    cb.like(cb.lower(r.get("city")), pattern),
                    cb.like(cb.lower(r.get("location")), pattern),
                    cb.like(cb.lower(r.get("area")), pattern)));
        }
        if (has(category)) {
            String pattern = "%" + category.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("category")), pattern));
        }
        if (has(type)) {
            String pattern = "%" + type.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("employmentType")), pattern));
        }
        if (has(experience)) {
            String pattern = "%" + experience.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("experienceLevel")), pattern));
        }
        if (hours != null && hours > 0) {
            OffsetDateTime cutoff = OffsetDateTime.now().minusHours(hours);
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("postedAt"), cutoff));
        }

        boolean hasCoordinates = latitude != null && longitude != null;
        boolean applyRadius = hasCoordinates && radiusKm != null && radiusKm > 0;
        List<JobListing> matches = repository.findAll(spec,
                Sort.by(Sort.Order.desc("postedAt"), Sort.Order.desc("discoveredAt")));

        // Apply the distance filter before the result cap so nearby matches are not hidden
        // by newer jobs outside the requested radius.
        if (applyRadius) {
            matches = matches.stream()
                    .filter(job -> job.getLatitude() != null && job.getLongitude() != null)
                    .filter(job -> GeoDistance.betweenKm(latitude, longitude,
                            job.getLatitude(), job.getLongitude()) <= radiusKm)
                    .toList();
        }
        int safeLimit = Math.max(1, Math.min(limit, 500));
        return matches.stream().limit(safeLimit)
                .map(job -> JobResponse.from(job,
                        hasCoordinates && job.getLatitude() != null && job.getLongitude() != null
                                ? GeoDistance.betweenKm(latitude, longitude,
                                        job.getLatitude(), job.getLongitude())
                                : null))
                .toList();
    }

    public long count() { return repository.count(); }

    private boolean has(String value) { return value != null && !value.isBlank(); }
}
