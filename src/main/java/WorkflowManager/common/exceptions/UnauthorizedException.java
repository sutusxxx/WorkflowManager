package WorkflowManager.common.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AppException {
    public UnauthorizedException() {
        super("Unauthorized", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
