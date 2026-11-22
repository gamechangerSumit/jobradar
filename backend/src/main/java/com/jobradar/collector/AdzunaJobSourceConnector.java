package com.jobradar.collector;
import com.fasterxml.jackson.databind.JsonNode;
import com.jobradar.domain.JobListing;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConditionalOnProperty(prefix="jobradar.sources.adzuna",name="enabled",havingValue="true")
public class AdzunaJobSourceConnector implements JobSourceConnector {
 private final RestClient client=RestClient.create();
 private final String appId,appKey,country,city;
 private final int radiusKm;
 public AdzunaJobSourceConnector(
  @Value("${jobradar.sources.adzuna.app-id:}") String appId,
  @Value("${jobradar.sources.adzuna.app-key:}") String appKey,
  @Value("${jobradar.sources.adzuna.country:in}") String country,
  @Value("${jobradar.sources.adzuna.city:Pune}") String city,
  @Value("${jobradar.sources.adzuna.radius-km:25}") int radiusKm
 ){this.appId=appId;this.appKey=appKey;this.country=country;this.city=city;this.radiusKm=radiusKm;}
 @Override public String sourceName(){return "Adzuna";}
 @Override public List<JobListing> fetchJobs(){
  if(appId.isBlank()||appKey.isBlank())return List.of();
  String url=UriComponentsBuilder.fromUriString("https://api.adzuna.com/v1/api/jobs/{country}/search/1")
   .queryParam("app_id",appId).queryParam("app_key",appKey).queryParam("where",city)
   .queryParam("distance",radiusKm).queryParam("results_per_page",50)
   .queryParam("sort_by","date").queryParam("content-type","application/json")
   .buildAndExpand(country).encode().toUriString();
  JsonNode response=client.get().uri(url).retrieve().body(JsonNode.class);
  List<JobListing> jobs=new ArrayList<>();
  if(response==null||!response.path("results").isArray())return jobs;
  for(JsonNode item:response.path("results")){
   String externalId=item.path("id").asText(""),title=item.path("title").asText("");
   if(externalId.isBlank()||title.isBlank())continue;
   JobListing job=new JobListing();job.setExternalId(externalId);job.setTitle(title);
   job.setCompany(item.path("company").path("display_name").asText(null));
   job.setLocation(item.path("location").path("display_name").asText(city));job.setCity(city);
   JsonNode areas=item.path("location").path("area");
   if(areas.isArray()&&areas.size()>0)job.setArea(areas.get(areas.size()-1).asText(null));
   String providerCategory=item.path("category").path("label").asText("Other");
   job.setCategory(normalizeCategory(title,providerCategory));
   String contractTime=item.path("contract_time").asText(""),contractType=item.path("contract_type").asText("");
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
 private String normalizeCategory(String title,String label){
  String value=(title+" "+label).toLowerCase(Locale.ROOT);
  if(value.matches(".*(devops|cloud engineer|site reliability|sre|kubernetes|aws engineer|azure engineer|platform engineer).*"))return "DevOps & Cloud";
  if(value.matches(".*(help ?desk|service desk|desktop support|it support|system administrator|linux administrator|technical support).*"))return "IT Support";
  String category=label.toLowerCase(Locale.ROOT);
  if(category.contains("it"))return "IT & Software";
  if(category.contains("account")||category.contains("finance"))return "Finance & Accounting";
  if(category.contains("sales")||category.contains("marketing"))return "Sales & Marketing";
  if(category.contains("health")||category.contains("nursing"))return "Healthcare";
  if(category.contains("teach")||category.contains("education"))return "Education";
  if(category.contains("engineer")||category.contains("construction"))return "Engineering";
  if(category.contains("manufactur")||category.contains("production"))return "Manufacturing";
  if(category.contains("logistic")||category.contains("warehouse"))return "Logistics & Delivery";
  if(category.contains("human resource")||category.equals("hr jobs"))return "Human Resources";
  if(category.contains("hospitality")||category.contains("catering"))return "Hospitality";
  if(category.contains("retail"))return "Retail & Customer Service";
  return "Other";
 }
}