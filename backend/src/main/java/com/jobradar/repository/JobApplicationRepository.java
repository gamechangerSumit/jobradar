package com.jobradar.repository;
import com.jobradar.domain.JobApplication;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long>{
 List<JobApplication> findAllByUser_IdOrderByUpdatedAtDesc(Long userId);
 Optional<JobApplication> findByUser_IdAndJob_Id(Long userId,Long jobId);
}