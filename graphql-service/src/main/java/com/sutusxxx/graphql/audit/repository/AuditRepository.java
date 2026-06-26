package com.sutusxxx.graphql.audit.repository;

import com.sutusxxx.graphql.audit.Audit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<Audit, String> {
}
