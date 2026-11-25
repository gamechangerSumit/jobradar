package com.jobradar.controller;
import com.jobradar.domain.*;
import com.jobradar.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/workspace")
public class WorkspaceController {
 private static final Set<String> STATUSES=Set.of("Interested","Applied","Interview","Offer","Rejected");
 private final UserAccountRepository users;private final JobListingRepository jobs;private final FavoriteJobRepository favorites;private final JobApplicationRepository applications;
 public WorkspaceController(UserAccountRepository users,JobListingRepository jobs,FavoriteJobRepository favorites,JobApplicationRepository applications){this.users=users;this.jobs=jobs;this.favorites=favorites;this.applications=applications;}
 @GetMapping public Map<String,Object> get(Authentication auth){
  UserAccount u=current(auth);
  List<Long> saved=favorites.findAllByUser_IdOrderBySavedAtDesc(u.getId()).stream().map(f->f.getJob().getId()).toList();
  Map<String,String> tracking=new LinkedHashMap<>();
  applications.findAllByUser_IdOrderByUpdatedAtDesc(u.getId()).forEach(a->tracking.put(String.valueOf(a.getJob().getId()),a.getStatus()));
  return Map.of("saved",saved,"tracking",tracking);
 }
 @PostMapping("/favorites/{jobId}") @ResponseStatus(HttpStatus.NO_CONTENT)
 public void save(@PathVariable Long jobId,Authentication auth){UserAccount u=current(auth);JobListing j=job(jobId);if(favorites.findByUser_IdAndJob_Id(u.getId(),jobId).isEmpty())favorites.save(new FavoriteJob(u,j));}
 @DeleteMapping("/favorites/{jobId}") @ResponseStatus(HttpStatus.NO_CONTENT)
 public void unsave(@PathVariable Long jobId,Authentication auth){favorites.findByUser_IdAndJob_Id(current(auth).getId(),jobId).ifPresent(favorites::delete);}
 public record StatusRequest(@NotBlank String status,@Size(max=2000) String notes){}
 @PutMapping("/applications/{jobId}") public Map<String,String> update(@PathVariable Long jobId,@Valid @RequestBody StatusRequest r,Authentication auth){
  if(!STATUSES.contains(r.status()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unsupported application status.");
  UserAccount u=current(auth);JobListing j=job(jobId);JobApplication a=applications.findByUser_IdAndJob_Id(u.getId(),jobId).orElseGet(()->new JobApplication(u,j,r.status()));
  a.setStatus(r.status());a.setNotes(r.notes());applications.save(a);return Map.of("status",a.getStatus());
 }
 @DeleteMapping("/applications/{jobId}") @ResponseStatus(HttpStatus.NO_CONTENT)
 public void remove(@PathVariable Long jobId,Authentication auth){applications.findByUser_IdAndJob_Id(current(auth).getId(),jobId).ifPresent(applications::delete);}
 private UserAccount current(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found."));}
 private JobListing job(Long id){return jobs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Job not found."));}
}