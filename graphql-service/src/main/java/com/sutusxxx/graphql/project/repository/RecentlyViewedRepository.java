package com.sutusxxx.graphql.project.repository;

import com.sutusxxx.graphql.project.RecentlyViewed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RecentlyViewedRepository extends MongoRepository<RecentlyViewed, String> {
    List<RecentlyViewed> findByUserIdOrderByLastViewedDesc(String userId, Pageable pageable);
    Optional<RecentlyViewed> findByUserIdAndProjectId(String userId, String projectId);
}
