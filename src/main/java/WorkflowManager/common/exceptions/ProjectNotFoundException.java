package WorkflowManager.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ProjectNotFoundException extends AppException {
    public ProjectNotFoundException(String key) {
        super("Project not found: " + key, HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND");
    }
}
