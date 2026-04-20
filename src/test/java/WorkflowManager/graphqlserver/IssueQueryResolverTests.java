package WorkflowManager.graphqlserver;

import WorkflowManager.graphql.issue.resolver.IssueQueryResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(IssueQueryResolver.class)
public class IssueQueryResolverTests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void shouldGetIssue() {
        graphQlTester
                .documentName("issueDetails")
                .variable("key", "DEV-1")
                .execute()
                .path("issueByKey")
                .matchesJson("""
                        {
                          "data": {
                            "issueByKey": {
                              "id": "1",
                              "key": "DEV-1",
                              "title": "User Authentication"
                            }
                          }
                        }
                        """);
    }
}
