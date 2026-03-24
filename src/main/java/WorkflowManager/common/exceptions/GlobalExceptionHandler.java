package WorkflowManager.common.exceptions;

import WorkflowManager.common.model.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles AppExceptions
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponseDTO> handleAppException(AppException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setStatus(ex.getStatus().value());
        error.setErrorCode(ex.getErrorCode());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidJson(HttpMessageNotReadableException ex) {
        String message = "Invalid request body";
        if (ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            String validValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            message = "Invalid value '%s' for field '%s'. Accepted: [%s]"
                    .formatted(ife.getValue(), ife.getPath().get(0).getPropertyName(), validValues);
        }

        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setStatus(400);
        error.setErrorCode("INVALID_REQUEST");
        error.setMessage(message);
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setStatus(500);
        error.setErrorCode("INTERNAL_ERROR");
        error.setMessage("An unexpected error occurred");
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.internalServerError().body(error);
    }
}
