package com.jobradar.collector;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobradar.domain.JobListing;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConditionalOnProperty(prefix="jobradar.sources.adzuna",name={"app-id","app-key"})
public class AdzunaJobSourceConnector implements JobSourceConnector {
 private final RestClient client=RestClient.create();
 private final String appId,appKey,country,city;
 private final int radiusKm;
 public AdzunaJobSourceConnector(
  @Value("${jobradar.sources.adzuna.app-id}") String appId,
  @Value("${jobradar.sources.adzuna.app-key}") String appKey,
  @Value("${jobradar.sources.adzuna.country:in}") String country,
  @Value("${jobradar.sources.adzuna.city:Pune}") String city,
  @Value("${jobradar.sources.adzuna.radius-km:25}") int radiusKm
 ){this.appId=appId;this.appKey=appKey;this.country=country;this.city=city;this.radiusKm=radiusKm;}
 @Override public String sourceName(){return "Adzuna";}
 @Override public List<JobListing> fetchJobs(){
  String url=UriComponentsBuilder.fromUriString("https://api.adzuna.com/v1/api/jobs/{country}/search/1")
   .queryParam("app_id",appId).queryParam("app_key",appKey)
   .queryParam("where",city).queryParam("distance",radiusKm)
   .queryParam("results_per_page",50).queryParam("sort_by","date")
   .queryParam("content-type","application/json").buildAndExpand(country).encode().toUriString();
  JsonNode response=client.get().uri(url).retrieve().body(JsonNode.class);
  List<JobListing> jobs=new ArrayList<>();
  if(response==null||!response.path("results").isArray())return jobs;
  for(JsonNode item:response.path("results")){
   String externalId=item.path("id").asText("");
   String title=item.path("title").asText("");
   if(externalId.isBlank()||title.isBlank())continue;
   JobListing job=new JobListing();
   job.setExternalId(externalId);job.setTitle(title);
   job.setCompany(item.path("company").path("display_name").asText(null));
   job.setLocation(item.path("location").path("display_name").asText(city));
   JsonNode areas=item.path("location").path("area");
   if(areas.isArray()&&areas.size()>0)job.setCity(areas.get(areas.size()-1).asText(city));else job.setCity(city);
   if(areas.isArray()&&areas.size()>1)job.setArea(areas.get(areas.size()-2).asText(null));
   job.setCategory(item.path("category").path("label").asText("Other"));
   String contractTime=item.path("contract_time").asText("");
   String contractType=item.path("contract_type").asText("");
   job.setEmploymentType(!contractTime.isBlank()?contractTime:contractType);
   job.setDescription(item.path("description").asText(""));
   job.setApplyUrl(item.path("redirect_url").asText(null));
   if(item.hasNonNull("latitude"))job.setLatitude(item.path("latitude").asDouble());
   if(item.hasNonNull("longitude"))job.setLongitude(item.path("longitude").asDouble());
   String created=item.path("created").asText("");
   if(!created.isBlank())try{job.setPostedAt(OffsetDateTime.parse(created));}catch(Exception ignored){}
   jobs.add(job);
  }
  return jobs;
 }
}