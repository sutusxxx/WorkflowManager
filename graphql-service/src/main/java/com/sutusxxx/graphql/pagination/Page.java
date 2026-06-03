package com.sutusxxx.graphql.pagination;

import java.util.List;

public record Page<T>(
        List<T> items,
        long total,
        int page,
        int pageSize
) {
}
