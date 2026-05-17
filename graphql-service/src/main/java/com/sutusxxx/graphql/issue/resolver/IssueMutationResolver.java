package com.sutusxxx.graphql.issue.resolver;

import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.IssueService;
import com.sutusxxx.graphql.issue.model.CreateIssueInput;
import com.sutusxxx.graphql.issue.model.TransitionIssueInput;
import com.sutusxxx.graphql.issue.model.UpdateIssueInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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
    public void deleteIssue(@Argument String id) {
        issueService.deleteIssue(id);
    }
}
