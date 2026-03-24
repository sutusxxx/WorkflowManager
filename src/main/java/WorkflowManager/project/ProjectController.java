package WorkflowManager.project;

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

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDTO> getProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{key}")
    public ProjectDTO getProjectByKey(@PathVariable String key) {
        return projectService.getProjectByKey(key);
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody CreateProjectRequest project) {
        ProjectDTO createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/v1/projects/create/" + createdProject.getKey())
                .body(createdProject);
    }
}
