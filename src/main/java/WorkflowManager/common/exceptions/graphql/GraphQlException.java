package WorkflowManager.common.exceptions.graphql;

import org.springframework.graphql.execution.ErrorType;

public abstract class GraphQlException extends RuntimeException {
    private final ErrorType errorType;

    public GraphQlException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
