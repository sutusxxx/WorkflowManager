package WorkflowManager.graphql.issue;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Status {
    private String id = UUID.randomUUID().toString();
    private String name;
    private StatusCategory category;
    private String color;
    private Integer displayOrder;
    private boolean isDefault = false;

    private List<String> allowedTransitionIds = new ArrayList<>();
}
