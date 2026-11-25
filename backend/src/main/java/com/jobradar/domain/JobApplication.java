package com.jobradar.domain;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="job_applications",uniqueConstraints=@UniqueConstraint(name="uk_application_user_job",columnNames={"user_id","job_id"}))
public class JobApplication {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private UserAccount user;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="job_id") private JobListing job;
 @Column(nullable=false,length=30) private String status;
 @Column(length=2000) private String notes;
 @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
 protected JobApplication(){}
 public JobApplication(UserAccount user,JobListing job,String status){this.user=user;this.job=job;this.status=status;}
 @PrePersist @PreUpdate void update(){updatedAt=OffsetDateTime.now();}
 public JobListing getJob(){return job;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public void setNotes(String v){notes=v;}
}