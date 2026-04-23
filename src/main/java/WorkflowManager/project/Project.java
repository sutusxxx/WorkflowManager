package WorkflowManager.project;
import WorkflowManager.issue.Status;
import lombok.Data;
import org.springframework.data.annotation.Id;
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
    private String key;

    private String name;
    private Integer issueCounter = 0;
    private String description;
    private List<Status> statuses = new ArrayList<>();
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt;
    private String createdBy;
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
