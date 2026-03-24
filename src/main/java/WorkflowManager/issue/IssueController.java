package WorkflowManager.issue;

import WorkflowManager.common.exceptions.IssueNotFoundException;
import WorkflowManager.issue.model.CreateIssueRequest;
import WorkflowManager.issue.model.IssueDTO;
import WorkflowManager.issue.model.IssueTreeDTO;
import WorkflowManager.issue.model.UpdateIssueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
public class IssueController {
    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public List<IssueDTO> getIssues(Long parentId, Long projectId, IssueType type, String status) {
        return issueService.getAllIssues(parentId, projectId, type, status);
    }

    @GetMapping("/{key}/tree")
    public IssueTreeDTO getTree(@PathVariable String key) {
        return issueService.getTree(key);
    }

    @GetMapping("/{key}")
    public IssueDTO getByKey(@PathVariable String key) {
        try {
            return issueService.getIssueByKey(key);
        } catch (IssueNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<IssueDTO> createIssue(@RequestBody CreateIssueRequest issue) {
        IssueDTO createdIssue = issueService.createIssue(issue);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/v1/issues/" + createdIssue.getKey())
                .body(createdIssue);
    }

    @PostMapping("/{key}/update")
    public IssueDTO updateIssue(@PathVariable String key, @RequestBody UpdateIssueRequest issue) {
        return issueService.updateIssue(key, issue);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }
}
