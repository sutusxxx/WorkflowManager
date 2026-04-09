package WorkflowManager.common.exceptions;

import org.springframework.http.HttpStatus;

public class BoardNotFoundException extends AppException {
    public BoardNotFoundException(Long id) {
        super("Board with id " + id + " not found", HttpStatus.NOT_FOUND, "BOARD_NOT_FOUND");
    }
}
