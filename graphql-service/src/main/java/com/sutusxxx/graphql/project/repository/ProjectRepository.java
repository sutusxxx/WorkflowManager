package com.sutusxxx.graphql.project.repository;

import com.sutusxxx.graphql.project.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
}
