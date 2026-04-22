package WorkflowManager.project.resolver;

import WorkflowManager.project.Project;
import WorkflowManager.project.ProjectService;
import WorkflowManager.project.model.CreateProjectInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectMutationResolver {
    private final ProjectService projectService;

    @Autowired
    public ProjectMutationResolver(ProjectService projectService) {
        this.projectService = projectService;
    }

    @MutationMapping
    public Project createProject(@Argument CreateProjectInput input) {
        return projectService.createProject(input);
    }
}
