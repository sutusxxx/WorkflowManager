package com.sutusxxx.graphql.issue.model;

import com.sutusxxx.graphql.issue.StatusCategory;

public record CreateStatusInput (
        String name,
        StatusCategory category,
        String color,
        Integer displayOrder,
        Boolean isDefault
) { }
