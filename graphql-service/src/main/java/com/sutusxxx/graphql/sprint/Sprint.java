package com.sutusxxx.graphql.sprint;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "sprints")
public class Sprint {
    @Id
    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String projectId;

    private String goal;

    private Instant startDate;
    private Instant endDate;

    private SprintState state;

    @CreatedDate
    private Instant createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Instant updatedAt;

    @LastModifiedBy
    private String modifiedBy;

    private Instant completedAt;
}
