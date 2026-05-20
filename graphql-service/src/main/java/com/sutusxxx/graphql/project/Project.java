package com.sutusxxx.graphql.project;
import com.sutusxxx.graphql.issue.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Document(collection = "projects")
public class Project {
    @Id
    private String id;

    @Indexed(unique = true)
    @NotEmpty
    private String key;

    @NotEmpty
    private String name;

    @Min(0)
    private Integer issueCounter = 0;
    private String description;
    private List<Status> statuses = new ArrayList<>();

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;

    private Visibility visibility;

    public Optional<Status> findStatusById(String statusId) {
        return statuses.stream().filter(status -> status.getId().equals(statusId)).findFirst();
    }

    public Status getDefaultStatus() {
        return statuses.stream()
                .filter(Status::isDefault)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No default status"));
    }
}
