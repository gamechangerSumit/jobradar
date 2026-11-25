package com.jobradar.repository;
import com.jobradar.domain.FavoriteJob;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FavoriteJobRepository extends JpaRepository<FavoriteJob,Long>{
 List<FavoriteJob> findAllByUser_IdOrderBySavedAtDesc(Long userId);
 Optional<FavoriteJob> findByUser_IdAndJob_Id(Long userId,Long jobId);
}