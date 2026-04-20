package WorkflowManager.graphql.issue.resolver;

import WorkflowManager.graphql.issue.IssueService;
import WorkflowManager.graphql.issue.model.IssueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class IssueQueryResolver {
    private final IssueService issueService;
    private static final Logger log = LoggerFactory.getLogger(IssueQueryResolver.class);

    @Autowired
    public IssueQueryResolver(IssueService issueService) {
        this.issueService = issueService;
    }

    @QueryMapping()
    public IssueDTO issueByKey(@Argument String key) {
        log.info("Get issue by key '{}'", key);
        return issueService.getIssueByKey(key);
    }
}
