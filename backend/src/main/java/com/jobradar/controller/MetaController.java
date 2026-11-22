package com.jobradar.controller;
import com.jobradar.service.JobService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api")
public class MetaController {
 private final JobService jobs;
 public MetaController(JobService jobs){this.jobs=jobs;}
 @GetMapping("/meta") public Map<String,Object> meta(){return Map.of("app","JobRadar AI","defaultCity","Pune","collectionIntervalMinutes",60,"totalJobs",jobs.count(),"categories",List.of("IT & Software","DevOps & Cloud","IT Support","Business & Management","Finance & Accounting","Sales & Marketing","Education","Healthcare","Manufacturing","Logistics & Delivery","Engineering","Human Resources","Hospitality","Retail & Customer Service","Other"));}
}