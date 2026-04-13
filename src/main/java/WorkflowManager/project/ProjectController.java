package WorkflowManager.project;

import WorkflowManager.issue.Issue;
import WorkflowManager.issue.IssueService;
import WorkflowManager.project.model.CreateProjectInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProjectController {
    private final ProjectService projectService;
    private final IssueService issueService;

    @Autowired
    public ProjectController(ProjectService projectService, IssueService issueService) {
        this.projectService = projectService;
        this.issueService = issueService;
    }

    @QueryMapping
    public List<Project> projects() {
        return projectService.getAllProjects();
    }

    @QueryMapping
    public Project projectById(@Argument Long id) {
        return projectService.getProjectById(id);
    }

    @MutationMapping
    public Project createProject(@Argument CreateProjectInput project) {
        return projectService.createProject(project);
    }

    @QueryMapping
    public List<Issue> issuesByProject(@Argument Long id) {
        return issueService.getIssuesByProjectId(id);
    }
}
