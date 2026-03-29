package WorkflowManager.common.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AppException {
    public UserNotFoundException() {
        super("User not found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}
