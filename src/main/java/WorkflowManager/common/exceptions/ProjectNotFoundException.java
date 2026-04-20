package WorkflowManager.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ProjectNotFoundException extends AppException {
    public ProjectNotFoundException(String id) {
        super("Project not found: " + id, HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND");
    }
}
