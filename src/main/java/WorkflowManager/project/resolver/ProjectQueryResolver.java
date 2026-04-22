package WorkflowManager.project.resolver;

import WorkflowManager.issue.IssueService;
import WorkflowManager.issue.model.IssueDTO;
import WorkflowManager.project.Project;
import WorkflowManager.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProjectQueryResolver {
    private final ProjectService projectService;
    private final IssueService issueService;

    @Autowired
    public ProjectQueryResolver(ProjectService projectService, IssueService issueService) {
        this.projectService = projectService;
        this.issueService = issueService;
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
    public List<IssueDTO> issues(Project project) {
        return issueService.getIssuesByProjectId(project.getId());
    }
}
