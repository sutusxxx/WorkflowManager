package com.sutusxxx.graphql.sprint.model;

import java.time.Instant;

public record CreateSprintInput(
        String name,
        Instant startDate,
        Instant endDate,
        String goal
) {
}
