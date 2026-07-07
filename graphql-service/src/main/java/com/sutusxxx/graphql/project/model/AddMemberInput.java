package com.sutusxxx.graphql.project.model;

import com.sutusxxx.graphql.project.ProjectRole;

public record AddMemberInput(
        String userId,
        String projectId,
        ProjectRole role
) {
}
