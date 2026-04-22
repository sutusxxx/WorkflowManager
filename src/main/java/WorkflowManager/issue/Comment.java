package WorkflowManager.issue;

import WorkflowManager.user.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private User createdBy;
    private Issue issue;
}
