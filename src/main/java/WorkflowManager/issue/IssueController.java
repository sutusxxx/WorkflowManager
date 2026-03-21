package WorkflowManager.issue;

import WorkflowManager.exceptions.IssueNotFoundException;
import WorkflowManager.issue.dtos.CreateIssueDTO;
import WorkflowManager.issue.dtos.IssueDTO;
import WorkflowManager.issue.dtos.UpdateIssueDTO;
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

    @GetMapping("/")
    public List<IssueDTO> getIssues(Long parentId, Long projectId, IssueType type, String status) {
        return issueService.getAllIssues(parentId, projectId, type, status);
    }

    @GetMapping("/{id}")
    public IssueDTO getById(@PathVariable Long id) {
        try {
            return issueService.getIssueById(id);
        } catch (IssueNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/create")
    public IssueDTO createIssue(@RequestBody CreateIssueDTO issue) {
        return issueService.createIssue(issue);
    }

    @PostMapping("/{id}/update")
    public IssueDTO updateIssue(@PathVariable Long id, @RequestBody UpdateIssueDTO issue) {
        return issueService.updateIssue(id, issue);
    }

    @DeleteMapping("/{id}")
    public void deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
    }
}
