package com.sutusxxx.graphql.issue.model;

import com.sutusxxx.graphql.issue.IssueLinkType;

public record RemoveIssueLinkInput(
        String sourceIssueId,
        String targetIssueId,
        IssueLinkType linkType
) {
}
