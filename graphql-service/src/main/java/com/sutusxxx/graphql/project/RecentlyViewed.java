package com.sutusxxx.graphql.project;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "recentlyViewed")
@CompoundIndex(
        name = "user_project_idx",
        def = "{'userId': 1, 'projectId': 1}",
        unique = true
)
public class RecentlyViewed {
    @Id
    private String id;
    private String userId;
    private String projectId;
    private Instant lastViewed;
}
