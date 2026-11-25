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
@Service @Transactional(readOnly=true)
public class JobService {
 private final JobListingRepository repository;
 public JobService(JobListingRepository repository){this.repository=repository;}
 public List<JobResponse> search(String keyword,String city,String category,String type,String experience,Integer hours,Double latitude,Double longitude,Double radiusKm,Integer minSalary,Integer maxSalary,int offset,int limit){
  Specification<JobListing> spec=(root,query,cb)->cb.conjunction();
  if(has(keyword)){String p="%"+keyword.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("title")),p),cb.like(cb.lower(r.get("company")),p),cb.like(cb.lower(r.get("description")),p)));}
  if(has(city)){String p="%"+city.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("city")),p),cb.like(cb.lower(r.get("location")),p),cb.like(cb.lower(r.get("area")),p),cb.equal(cb.lower(r.get("city")),"remote"),cb.like(cb.lower(r.get("location")),"%worldwide%"),cb.like(cb.lower(r.get("location")),"%anywhere%")));}
  if(has(category)){String p="%"+category.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.like(cb.lower(r.get("category")),p));}
  if(has(type)){String p="%"+type.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.like(cb.lower(r.get("employmentType")),p));}
  if(has(experience)){String p="%"+experience.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.like(cb.lower(r.get("experienceLevel")),p));}
  if(hours!=null&&hours>0){OffsetDateTime cutoff=OffsetDateTime.now().minusHours(hours);spec=spec.and((r,q,cb)->cb.greaterThanOrEqualTo(r.get("postedAt"),cutoff));}
  if(minSalary!=null&&minSalary>0)spec=spec.and((r,q,cb)->cb.or(cb.greaterThanOrEqualTo(r.get("salaryMax"),minSalary.doubleValue()),cb.greaterThanOrEqualTo(r.get("salaryMin"),minSalary.doubleValue())));
  if(maxSalary!=null&&maxSalary>0)spec=spec.and((r,q,cb)->cb.or(cb.lessThanOrEqualTo(r.get("salaryMin"),maxSalary.doubleValue()),cb.lessThanOrEqualTo(r.get("salaryMax"),maxSalary.doubleValue())));
  boolean coords=latitude!=null&&longitude!=null,rad=coords&&radiusKm!=null&&radiusKm>0;List<JobListing> matches=repository.findAll(spec,Sort.by(Sort.Order.desc("postedAt"),Sort.Order.desc("discoveredAt")));
  if(rad)matches=matches.stream().filter(j->j.getLatitude()!=null&&j.getLongitude()!=null).filter(j->GeoDistance.betweenKm(latitude,longitude,j.getLatitude(),j.getLongitude())<=radiusKm).toList();
  int safeOffset=Math.max(0,Math.min(offset,100000)),safeLimit=Math.max(1,Math.min(limit,100));
  return matches.stream().skip(safeOffset).limit(safeLimit).map(j->JobResponse.from(j,coords&&j.getLatitude()!=null&&j.getLongitude()!=null?GeoDistance.betweenKm(latitude,longitude,j.getLatitude(),j.getLongitude()):null)).toList();
 }
 public long count(){return repository.count();}
 private boolean has(String v){return v!=null&&!v.isBlank();}
}