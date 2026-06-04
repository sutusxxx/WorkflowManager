package com.sutusxxx.graphql.project.model;

public record AddTransitionInput(
        String fromStatusId,
        String toStatusId
) {
}
