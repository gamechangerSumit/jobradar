package com.jobradar.scheduler;
import com.jobradar.service.JobCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
public class JobCollectionScheduler {
 private static final Logger log=LoggerFactory.getLogger(JobCollectionScheduler.class);
 private final JobCollectionService collection;
 public JobCollectionScheduler(JobCollectionService collection){this.collection=collection;}
 @Scheduled(fixedDelayString="${jobradar.collection.interval-ms:3600000}",initialDelayString="${jobradar.collection.initial-delay-ms:30000}")
 public void collect(){log.info("Scheduled discovery complete; {} records processed from {} configured sources.",collection.collect(),collection.configuredSourceCount());}
}