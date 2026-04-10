package WorkflowManager.issue;

import WorkflowManager.issue.model.CreateIssueInput;
import WorkflowManager.issue.model.UpdateIssueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


@Controller
public class IssueController {
    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @QueryMapping
    public Issue issueByKey(@Argument String key) {
        return issueService.getIssueByKey(key);
    }

    @MutationMapping
    public Issue createIssue(@Argument CreateIssueInput input) {
        return issueService.createIssue(input);
    }

    @MutationMapping
    public Issue updateIssue(@Argument Long id, @Argument UpdateIssueRequest issue) {
        return issueService.updateIssue(id, issue);
    }

    @MutationMapping
    public void deleteIssue(@Argument Long id) {
        issueService.deleteIssue(id);
    }
}
