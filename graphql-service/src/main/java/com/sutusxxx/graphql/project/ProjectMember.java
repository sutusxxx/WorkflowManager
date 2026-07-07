package com.sutusxxx.graphql.project;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ProjectMember {
    private String userId;
    private ProjectRole role;
    private Instant joinedAt = Instant.now();
}
