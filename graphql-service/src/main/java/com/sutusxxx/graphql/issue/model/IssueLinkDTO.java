package com.sutusxxx.graphql.issue.model;

import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueLinkType;
import lombok.Data;

@Data
public class IssueLinkDTO {
    private Issue source;
    private Issue target;
    private IssueLinkType linkType;
}
