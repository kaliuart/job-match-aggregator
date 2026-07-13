package com.artur.jobaggregator.repository;

import java.util.Optional;

import com.artur.jobaggregator.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository <JobEntity, Long> {
    @Query(
            "SELECT j FROM JobEntity j " +
            "WHERE (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT ('%', CAST(:keyword AS String), '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT ('%', CAST(:keyword AS String), '%'))) " +
            "AND (:remote is NULL OR j.remote = :remote) " +
            "AND (:location is NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', CAST(:location AS String), '%')))"
    )
    Page<JobEntity> search(@Param("keyword") String keyword,
                           @Param("location") String location,
                           @Param("remote") Boolean remote,
                           Pageable pageable);

    Optional<JobEntity> findBySlug(String slug);
}
