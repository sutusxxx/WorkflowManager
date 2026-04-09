package WorkflowManager.issue;

import WorkflowManager.common.exceptions.IssueNotFoundException;
import WorkflowManager.issue.model.CreateIssueRequest;
import WorkflowManager.issue.model.IssueDTO;
import WorkflowManager.issue.model.IssueTreeDTO;
import WorkflowManager.issue.model.UpdateIssueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class IssueController {
    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    public List<IssueDTO> getIssues(Long parentId, Long projectId, IssueType type, String status) {
        return issueService.getAllIssues(parentId, projectId, type, status);
    }

    public IssueTreeDTO getTree(Long id) {
        return issueService.getTree(id);
    }

    @QueryMapping
    public IssueDTO issueByKey(@Argument String key) {
        try {
            return issueService.getIssueByKey(key);
        } catch (IssueNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<IssueDTO> createIssue(CreateIssueRequest issue) {
        IssueDTO createdIssue = issueService.createIssue(issue);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/v1/issues/" + createdIssue.getId())
                .body(createdIssue);
    }

    public IssueDTO updateIssue(Long id, UpdateIssueRequest issue) {
        return issueService.updateIssue(id, issue);
    }

    public ResponseEntity<Void> deleteIssue(Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }
}
