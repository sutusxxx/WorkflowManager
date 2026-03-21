package WorkflowManager.project;

import WorkflowManager.project.dtos.CreateProjectDTO;
import WorkflowManager.project.dtos.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PutMapping("/create")
    public ProjectDTO createProject(@RequestBody CreateProjectDTO project) {
        return projectService.createProject(project);
    }
}
