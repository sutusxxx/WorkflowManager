package WorkflowManager.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class IssueNotFoundException extends AppException {
    public IssueNotFoundException(String key) {
        super("Issue not found: " + key, HttpStatus.NOT_FOUND, "ISSUE_NOT_FOUND");
    }
}
