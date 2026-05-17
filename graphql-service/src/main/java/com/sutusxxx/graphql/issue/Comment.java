package com.sutusxxx.graphql.issue;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private String createdBy;
    private Issue issue;
}
