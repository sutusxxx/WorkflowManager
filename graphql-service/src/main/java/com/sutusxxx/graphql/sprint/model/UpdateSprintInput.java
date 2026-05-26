package com.sutusxxx.graphql.sprint.model;

import java.time.Instant;

public record UpdateSprintInput(
        String name,
        String goal,
        Instant startDate,
        Instant endDate
) {
}
