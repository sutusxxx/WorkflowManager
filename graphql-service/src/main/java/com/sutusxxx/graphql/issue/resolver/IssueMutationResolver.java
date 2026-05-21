package com.sutusxxx.graphql.issue.resolver;

import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
import com.sutusxxx.graphql.issue.model.*;
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
    public Issue createIssue(@Argument CreateIssueInput input) {
        return issueService.createIssue(input);
    }

    @MutationMapping
    public Issue updateIssue(@Argument String id, @Argument UpdateIssueInput input) {
        return issueService.updateIssue(id, input);
    }

    @MutationMapping
    public Issue changeStatus(@Argument String issueId, @Argument TransitionIssueInput input) {
        return issueService.changeStatus(issueId, input.newStatusId());
    }

    @MutationMapping
    public Boolean deleteIssue(@Argument String id) {
        return issueService.deleteIssue(id);
    }

    @MutationMapping
    public Issue linkIssue(@Argument AddIssueLinkInput input) {
        return issueService.addLink(input);
    }

    @MutationMapping
    public Issue removeLink(@Argument RemoveIssueLinkInput input) {
        return issueService.removeLink(input);
    }
}
