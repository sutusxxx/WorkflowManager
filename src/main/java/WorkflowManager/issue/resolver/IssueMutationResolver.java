package WorkflowManager.issue.resolver;

import WorkflowManager.issue.Issue;
import WorkflowManager.issue.IssueService;
import WorkflowManager.issue.model.CreateIssueInput;
import WorkflowManager.issue.model.TransitionIssueInput;
import WorkflowManager.issue.model.UpdateIssueInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
public class IssueMutationResolver {
    private final IssueService issueService;
    private static final Logger log = LoggerFactory.getLogger(IssueMutationResolver.class);

    @Autowired
    public IssueMutationResolver(IssueService issueService) {
        this.issueService = issueService;
    }

    @MutationMapping
    public Issue createIssue(@Argument CreateIssueInput input, @AuthenticationPrincipal UserDetails user) {
        return issueService.createIssue(input, user);
    }

    @MutationMapping
    public Issue updateIssue(@Argument String id, @Argument UpdateIssueInput input, @AuthenticationPrincipal UserDetails user) {
        return issueService.updateIssue(id, input, user);
    }

    @MutationMapping
    public Issue changeStatus(@Argument String issueId, @Argument TransitionIssueInput input) {
        return issueService.changeStatus(issueId, input.newStatusId());
    }

    @MutationMapping
    public void deleteIssue(@Argument String id) {
        issueService.deleteIssue(id);
    }
}
