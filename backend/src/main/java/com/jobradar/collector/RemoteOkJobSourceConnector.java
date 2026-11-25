package com.jobradar.collector;
import com.fasterxml.jackson.databind.JsonNode;
import com.jobradar.domain.JobListing;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
@Component
@ConditionalOnProperty(prefix="jobradar.sources.remoteok",name="enabled",havingValue="true",matchIfMissing=true)
public class RemoteOkJobSourceConnector implements JobSourceConnector {
 private final RestClient client=RestClient.builder().defaultHeader("User-Agent","JobRadarAI/1.0").build();
 public String sourceName(){return "RemoteOK";}
 public List<JobListing> fetchJobs(){
  JsonNode body=client.get().uri("https://remoteok.com/api").retrieve().body(JsonNode.class);List<JobListing> out=new ArrayList<>();
  if(body==null||!body.isArray())return out;
  for(JsonNode x:body){String id=x.path("id").asText(""),title=x.path("position").asText("");if(id.isBlank()||title.isBlank())continue;
   JobListing j=new JobListing();j.setExternalId(id);j.setTitle(title);j.setCompany(x.path("company").asText(null));j.setCity("Remote");
   String loc=x.path("location").asText("Worldwide remote");j.setLocation(loc.isBlank()?"Worldwide remote":loc+" · Remote");j.setCategory(category(title+" "+x.path("tags").toString()));j.setEmploymentType("Remote");j.setDescription(x.path("description").asText(""));j.setApplyUrl(x.path("url").asText(null));
   if(x.path("salary_min").isNumber())j.setSalaryMin(x.path("salary_min").asDouble());if(x.path("salary_max").isNumber())j.setSalaryMax(x.path("salary_max").asDouble());if(j.getSalaryMin()!=null||j.getSalaryMax()!=null)j.setSalaryCurrency("USD");
   String date=x.path("date").asText("");if(!date.isBlank())try{j.setPostedAt(OffsetDateTime.parse(date));}catch(Exception ignored){}out.add(j);
  }return out;
 }
 static String category(String text){String s=text.toLowerCase(Locale.ROOT);if(s.matches("(?s).*(devops|cloud|sre|kubernetes|platform engineer|site reliability).*"))return "DevOps & Cloud";if(s.matches("(?s).*(support|help desk|service desk|administrator).*"))return "IT Support";if(s.matches("(?s).*(software|developer|engineer|programmer|backend|frontend|full.?stack).*"))return "IT & Software";if(s.contains("design"))return "Design & Creative";if(s.contains("sales"))return "Sales & Marketing";return "Other";}
}