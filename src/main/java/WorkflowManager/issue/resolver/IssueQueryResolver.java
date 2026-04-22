package WorkflowManager.issue.resolver;

import WorkflowManager.issue.Issue;
import WorkflowManager.issue.IssueService;
import WorkflowManager.user.UserService;
import WorkflowManager.user.model.UserSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class IssueQueryResolver {
    private final IssueService issueService;
    private final UserService userService;

    @Autowired
    public IssueQueryResolver(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    @QueryMapping()
    public Issue issueByKey(@Argument String key) {
        return issueService.getIssueByKey(key);
    }

    @BatchMapping(typeName = "Issue", field = "children")
    public Map<Issue, List<Issue>> subIssues(List<Issue> issues) {
        return issueService.loadChildren(issues);
    }

    @BatchMapping(typeName = "Issue", field = "createdBy")
    public Map<Issue, UserSummaryDTO> createdBy(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getCreatedBy);
    }

    @BatchMapping(typeName = "Issue", field = "modifiedBy")
    public Map<Issue, UserSummaryDTO> modifiedBy(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getModifiedBy);
    }

    @BatchMapping(typeName = "Issue", field = "assigned")
    public Map<Issue, UserSummaryDTO> assigned(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getAssignee);
    }

    @BatchMapping(typeName = "Issue", field = "reporter")
    public Map<Issue, UserSummaryDTO> reporter(List<Issue> issues) {
        return userService.batchLoadUsers(issues, Issue::getReporter);
    }
}
