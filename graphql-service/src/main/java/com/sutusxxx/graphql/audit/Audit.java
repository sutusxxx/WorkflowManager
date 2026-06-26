package com.sutusxxx.graphql.audit;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "audits")
public class Audit {
    @Id
    private String id;

    private String action;
    private String entity;
    private String entityId;

    @CreatedDate
    private Instant timestamp;

    @CreatedBy
    private String userId;
}
