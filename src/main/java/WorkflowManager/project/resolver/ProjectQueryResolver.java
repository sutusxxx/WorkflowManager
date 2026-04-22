package WorkflowManager.project.resolver;

import WorkflowManager.issue.Issue;
import WorkflowManager.issue.IssueService;
import WorkflowManager.project.Project;
import WorkflowManager.project.ProjectService;
import WorkflowManager.user.UserService;
import WorkflowManager.user.model.UserSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ProjectQueryResolver {
    private final ProjectService projectService;
    private final IssueService issueService;
    private final UserService userService;

    @Autowired
    public ProjectQueryResolver(ProjectService projectService, IssueService issueService, UserService userService) {
        this.projectService = projectService;
        this.issueService = issueService;
        this.userService = userService;
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
    public List<Issue> issues(Project project) {
        return issueService.getIssuesByProjectId(project.getId());
    }

    @BatchMapping(typeName = "Project", field = "createdBy")
    public Map<Project, UserSummaryDTO> createdBy(List<Project> projects) {
        return userService.batchLoadUsers(projects, Project::getCreatedBy);
    }

    @BatchMapping(typeName = "Project", field = "modifiedBy")
    public Map<Project, UserSummaryDTO> modifiedBy(List<Project> projects) {
        return userService.batchLoadUsers(projects, Project::getModifiedBy);
    }
}
