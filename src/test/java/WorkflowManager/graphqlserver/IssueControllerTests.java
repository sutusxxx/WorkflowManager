package WorkflowManager.graphqlserver;

import WorkflowManager.issue.IssueController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(IssueController.class)
public class IssueControllerTests {

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
