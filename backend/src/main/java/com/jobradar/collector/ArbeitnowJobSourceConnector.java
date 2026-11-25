package com.jobradar.collector;
import com.fasterxml.jackson.databind.JsonNode;
import com.jobradar.domain.JobListing;
import java.time.*;
import java.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
@Component
@ConditionalOnProperty(prefix="jobradar.sources.arbeitnow",name="enabled",havingValue="true",matchIfMissing=true)
public class ArbeitnowJobSourceConnector implements JobSourceConnector {
 private final RestClient client=RestClient.builder().defaultHeader("User-Agent","JobRadarAI/1.0").build();
 public String sourceName(){return "Arbeitnow";}
 public List<JobListing> fetchJobs(){
  JsonNode body=client.get().uri("https://www.arbeitnow.com/api/job-board-api?page=1").retrieve().body(JsonNode.class);List<JobListing> out=new ArrayList<>();JsonNode data=body==null?null:body.path("data");if(data==null||!data.isArray())return out;
  for(JsonNode x:data){String id=x.path("slug").asText(""),title=x.path("title").asText("");if(id.isBlank()||title.isBlank())continue;
   JobListing j=new JobListing();j.setExternalId(id);j.setTitle(title);j.setCompany(x.path("company_name").asText(null));boolean remote=x.path("remote").asBoolean(false);String loc=x.path("location").asText("Europe");j.setCity(remote?"Remote":loc);j.setLocation(remote?loc+" · Remote":loc);
   j.setCategory(RemoteOkJobSourceConnector.category(title+" "+x.path("tags").toString()));JsonNode types=x.path("job_types");j.setEmploymentType(types.isArray()&&types.size()>0?types.get(0).asText("Full-time"):"Full-time");j.setDescription(x.path("description").asText(""));j.setApplyUrl(x.path("url").asText(null));
   long created=x.path("created_at").asLong(0);if(created>0)j.setPostedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(created),ZoneOffset.UTC));out.add(j);
  }return out;
 }
}