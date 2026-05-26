package com.sutusxxx.graphql.sprint.repository;

import com.sutusxxx.graphql.sprint.Sprint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SprintRepository extends MongoRepository<Sprint, String> {
    List<Sprint> findByProjectId(String projectId);

    @Query("{ 'projectId': ?0, 'active': true }")
    Optional<Sprint> findActiveByProjectId(String projectId);
}
