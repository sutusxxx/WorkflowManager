package WorkflowManager.common.exceptions.graphql;

import graphql.GraphQLError;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import org.jspecify.annotations.Nullable;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;

public class GraphGlExceptionHandler extends DataFetcherExceptionResolverAdapter {
    @Override
    protected @Nullable GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        return GraphQLError.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An internal error occurred")
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
