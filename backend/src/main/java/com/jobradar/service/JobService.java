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
@Transactional(readOnly=true)
public class JobService {
 private final JobListingRepository repo;
 public JobService(JobListingRepository repo){this.repo=repo;}
 public List<JobResponse> search(String keyword,String city,String category,String type,String experience,Integer hours,Double lat,Double lon,Double radius,int limit){
  Specification<JobListing> spec=(root,q,cb)->cb.conjunction();
  if(has(keyword)){String p="%"+keyword.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("title")),p),cb.like(cb.lower(r.get("company")),p),cb.like(cb.lower(r.get("description")),p)));}
  if(has(city)){String p="%"+city.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("city")),p),cb.like(cb.lower(r.get("location")),p),cb.like(cb.lower(r.get("area")),p)));}
  if(has(category)){String p="%"+category.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.like(cb.lower(r.get("category")),p));}
  if(has(type)){String p="%"+type.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.like(cb.lower(r.get("employmentType")),p));}
  if(has(experience)){String p="%"+experience.trim().toLowerCase(Locale.ROOT)+"%";spec=spec.and((r,q,cb)->cb.like(cb.lower(r.get("experienceLevel")),p));}
  if(hours!=null&&hours>0){OffsetDateTime cutoff=OffsetDateTime.now().minusHours(hours);spec=spec.and((r,q,cb)->cb.greaterThanOrEqualTo(r.get("postedAt"),cutoff));}
  boolean hasGeo=lat!=null&&lon!=null;
  List<JobListing> found=repo.findAll(spec,Sort.by(Sort.Order.desc("postedAt"),Sort.Order.desc("discoveredAt"))).stream().limit(Math.max(1,Math.min(limit,500))).toList();
  if(hasGeo&&radius!=null&&radius>0)found=found.stream().filter(j->j.getLatitude()!=null&&j.getLongitude()!=null&&GeoDistance.betweenKm(lat,lon,j.getLatitude(),j.getLongitude())<=radius).toList();
  return found.stream().map(j->JobResponse.from(j,hasGeo&&j.getLatitude()!=null&&j.getLongitude()!=null?GeoDistance.betweenKm(lat,lon,j.getLatitude(),j.getLongitude()):null)).toList();
 }
 public long count(){return repo.count();}
 private boolean has(String s){return s!=null&&!s.isBlank();}
}