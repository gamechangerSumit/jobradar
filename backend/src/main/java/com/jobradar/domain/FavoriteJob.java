package com.jobradar.domain;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="favorite_jobs",uniqueConstraints=@UniqueConstraint(name="uk_favorite_user_job",columnNames={"user_id","job_id"}))
public class FavoriteJob {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private UserAccount user;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="job_id") private JobListing job;
 @Column(name="saved_at",nullable=false) private OffsetDateTime savedAt;
 protected FavoriteJob(){}
 public FavoriteJob(UserAccount user,JobListing job){this.user=user;this.job=job;}
 @PrePersist void create(){savedAt=OffsetDateTime.now();}
 public Long getId(){return id;} public JobListing getJob(){return job;}
}