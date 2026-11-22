package com.jobradar.controller;
import com.jobradar.dto.JobResponse;
import com.jobradar.service.JobService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobController {
 private final JobService service;
 public JobController(JobService service){this.service=service;}
 @GetMapping public List<JobResponse> search(@RequestParam(required=false)String keyword,@RequestParam(defaultValue="Pune")String city,@RequestParam(required=false)String category,@RequestParam(required=false)String employmentType,@RequestParam(required=false)String experience,@RequestParam(required=false)@Min(1)@Max(8760)Integer postedWithinHours,@RequestParam(required=false)Double latitude,@RequestParam(required=false)Double longitude,@RequestParam(required=false)@Min(1)@Max(500)Double radiusKm,@RequestParam(defaultValue="100")@Min(1)@Max(500)int limit){
  return service.search(keyword,city,category,employmentType,experience,postedWithinHours,latitude,longitude,radiusKm,limit);
 }
}