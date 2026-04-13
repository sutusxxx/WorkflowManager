package WorkflowManager.project;

import WorkflowManager.auth.AuthContext;
import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.dao.IssueDAO;
import WorkflowManager.project.dao.ProjectDAO;
import WorkflowManager.project.model.CreateProjectInput;
import WorkflowManager.project.model.UpdateProjectInput;
import WorkflowManager.issue.IssueConverter;
import WorkflowManager.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectDAO projectDAO;

    private final ProjectConverter projectConverter;

    private final AuthContext authContext;

    public ProjectService(
            ProjectDAO projectDAO,
            IssueDAO issueDAO,
            ProjectConverter projectConverter,
            AuthContext authContext
    ) {
        this.projectDAO = projectDAO;
        this.projectConverter = projectConverter;
        this.authContext = authContext;
    }

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    public Project getProjectById(Long id) {
        return projectDAO.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Transactional
    public Project createProject(CreateProjectInput input) {
        User currentUser = authContext.getCurrentUser();

        Project project = projectConverter.convertFromInput(input);
        project.setVisibility(input.getPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);

        project.setCreatedBy(currentUser);

        return projectDAO.save(project);
    }

    @Transactional
    public Project updateProject(Long id, UpdateProjectInput input) {
        User currentUser = authContext.getCurrentUser();

        Project project = projectDAO.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));

        if (input.getDescription() != null && !input.getDescription().equals(project.getDescription())) {
            project.setDescription(input.getDescription());
        }

        project.setVisibility(input.getPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);

        project.setModifiedBy(currentUser);

        return project;
    }
}
