package com.sutusxxx.graphql.project.model;

public record UpdateProjectInput(
        String name,
        String description,
        Boolean isPrivate
) { }
