package WorkflowManager.common.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidHierarchyException extends AppException {
    public InvalidHierarchyException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_CONTENT, "INVALID_HIERARCHY");
    }
}
