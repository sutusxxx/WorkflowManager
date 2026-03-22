package WorkflowManager.issue;

import WorkflowManager.exceptions.IssueNotFoundException;
import WorkflowManager.issue.models.CreateIssueRequest;
import WorkflowManager.issue.models.IssueDTO;
import WorkflowManager.issue.models.IssueTreeDTO;
import WorkflowManager.issue.models.UpdateIssueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/create")
    public IssueDTO createIssue(@RequestBody CreateIssueRequest issue) {
        return issueService.createIssue(issue);
    }

    @PostMapping("/{id}/update")
    public IssueDTO updateIssue(@PathVariable Long id, @RequestBody UpdateIssueRequest issue) {
        return issueService.updateIssue(id, issue);
    }

    @DeleteMapping("/{id}")
    public void deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
    }
}
