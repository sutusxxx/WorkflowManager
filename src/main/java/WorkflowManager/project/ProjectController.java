package WorkflowManager.project;

import WorkflowManager.issue.IssueService;
import WorkflowManager.issue.model.IssueSummaryDTO;
import WorkflowManager.project.model.CreateProjectRequest;
import WorkflowManager.project.model.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final IssueService issueService;

    @Autowired
    public ProjectController(ProjectService projectService, IssueService issueService) {
        this.projectService = projectService;
        this.issueService = issueService;
    }

    @GetMapping
    public List<ProjectDTO> getProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDTO getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody CreateProjectRequest project) {
        ProjectDTO createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/v1/projects/create/" + createdProject.getId())
                .body(createdProject);
    }

    @GetMapping("/{id}/issues")
    public List<IssueSummaryDTO> getIssuesByProject(@PathVariable Long id) {
        return issueService.getIssuesByProjectId(id);
    }
}
