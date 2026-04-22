package WorkflowManager.issue.resolver;

import WorkflowManager.issue.IssueService;
import WorkflowManager.issue.model.IssueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(IssueQueryResolver.class);

    @Autowired
    public IssueQueryResolver(IssueService issueService) {
        this.issueService = issueService;
    }

    @QueryMapping()
    public IssueDTO issueByKey(@Argument String key) {
        return issueService.getIssueByKey(key);
    }

    @BatchMapping(typeName = "Issue", field = "children")
    public Map<IssueDTO, List<IssueDTO>> subIssues(List<IssueDTO> issues) {
        return issueService.loadChildren(issues);
    }
}
