package WorkflowManager.graphql.project.resolver;

import WorkflowManager.graphql.issue.Issue;
import WorkflowManager.graphql.project.Project;
import WorkflowManager.graphql.project.ProjectService;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class ProjectQueryResolver {
    private final ProjectService projectService;

    @Autowired
    public ProjectQueryResolver(ProjectService projectService) {
        this.projectService = projectService;
    }

    @QueryMapping
    public List<Project> projects() {
        return projectService.getAllProjects();
    }

    @QueryMapping
    public Project projectById(@Argument String id) {
        return projectService.getProjectById(id);
    }

    @SchemaMapping(typeName = "Project", field = "issues")
    public CompletableFuture<List<Issue>> issues(Project project, DataLoader<Long, List<Issue>> loader) {
        return null;
    }
}
