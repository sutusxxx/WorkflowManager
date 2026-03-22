package WorkflowManager.project;

import WorkflowManager.project.models.CreateProjectRequest;
import WorkflowManager.project.models.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ProjectDTO getProjectByKey(String key) {
        return projectService.getProjectByKey(key);
    }

    @PutMapping("/create")
    public ProjectDTO createProject(@RequestBody CreateProjectRequest project) {
        return projectService.createProject(project);
    }
}
