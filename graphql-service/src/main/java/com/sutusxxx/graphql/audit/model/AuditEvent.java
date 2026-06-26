package com.sutusxxx.graphql.audit.model;

import java.util.Map;

public record AuditEvent(
        String action,
        String entityType,
        String entityId
) {
}
