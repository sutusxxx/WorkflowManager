package WorkflowManager.graphql.project.resolver;

import WorkflowManager.graphql.project.Project;
import WorkflowManager.graphql.project.ProjectService;
import WorkflowManager.graphql.project.model.CreateProjectInput;
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
    public Project createProject(@Argument CreateProjectInput project) {
        return projectService.createProject(project);
    }
}
