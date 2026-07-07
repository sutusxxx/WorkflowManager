package com.sutusxxx.graphql.issue.resolver;

import com.sutusxxx.graphql.annotation.CurrentUser;
import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
import com.sutusxxx.graphql.issue.model.*;
import com.sutusxxx.user.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class IssueMutationResolver {
    private final IssueService issueService;

    @Autowired
    public IssueMutationResolver(IssueService issueService) {
        this.issueService = issueService;
    }

    @MutationMapping
    public Issue createIssue(@Argument CreateIssueInput input, @CurrentUser User currentUser) {
        return issueService.createIssue(currentUser, input);
    }

    @MutationMapping
    public Issue updateIssue(@Argument String id, @Argument UpdateIssueInput input, @CurrentUser User currentUser) {
        return issueService.updateIssue(currentUser, id, input);
    }

    @MutationMapping
    public Issue changeStatus(@Argument String issueId, @Argument TransitionIssueInput input, @CurrentUser User currentUser) {
        return issueService.changeStatus(currentUser, issueId, input.newStatusId());
    }

    @MutationMapping
    public String deleteIssue(@Argument String id, @CurrentUser User currentUser) {
        return issueService.deleteIssue(currentUser, id);
    }

    @MutationMapping
    public Issue linkIssue(@Argument AddIssueLinkInput input, @CurrentUser User currentUser) {
        return issueService.addLink(currentUser, input);
    }

    @MutationMapping
    public Issue removeLink(@Argument RemoveIssueLinkInput input, @CurrentUser User currentUser) {
        return issueService.removeLink(currentUser, input);
    }
}
