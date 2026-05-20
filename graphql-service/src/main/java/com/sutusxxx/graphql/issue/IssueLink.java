package com.sutusxxx.graphql.issue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueLink {
    private String targetIssueId;
    private IssueLinkType linkType;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private String createdBy;
}
