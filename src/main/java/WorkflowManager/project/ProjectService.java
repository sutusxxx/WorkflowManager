package WorkflowManager.project;

import WorkflowManager.auth.AuthContext;
import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.Issue;
import WorkflowManager.issue.dao.IssueDAO;
import WorkflowManager.project.dao.ProjectDAO;
import WorkflowManager.project.model.CreateProjectRequest;
import WorkflowManager.project.model.ProjectDTO;
import WorkflowManager.project.model.UpdateProjectRequest;
import WorkflowManager.issue.IssueConverter;
import WorkflowManager.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectDAO projectDAO;
    private final IssueDAO issueDAO;

    private final ProjectConverter projectConverter;
    private final IssueConverter issueConverter;

    private final AuthContext authContext;

    public ProjectService(
            ProjectDAO projectDAO,
            IssueDAO issueDAO,
            ProjectConverter projectConverter,
            IssueConverter issueConverter, AuthContext authContext
    ) {
        this.projectDAO = projectDAO;
        this.issueDAO = issueDAO;
        this.projectConverter = projectConverter;
        this.issueConverter = issueConverter;
        this.authContext = authContext;
    }

    public List<ProjectDTO> getAllProjects() {
        return projectDAO.findAll().stream().map(projectConverter::convertToDTO).toList();
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectDAO.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
        return projectConverter.convertToDTO(project);
    }

    @Transactional
    public ProjectDTO createProject(CreateProjectRequest request) {
        User currentUser = authContext.getCurrentUser();

        Project project = projectConverter.convertFromRequest(request);
        project.setVisibility(request.getPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);

        project.setCreatedBy(currentUser);

        Project savedProject = projectDAO.save(project);

        return projectConverter.convertToDTO(savedProject);
    }

    @Transactional
    public ProjectDTO updateProject(Long id, UpdateProjectRequest request) {
        User currentUser = authContext.getCurrentUser();

        Project project = projectDAO.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));

        if (request.getDescription() != null && !request.getDescription().equals(project.getDescription())) {
            project.setDescription(request.getDescription());
        }

        project.setVisibility(request.getPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);

        project.setModifiedBy(currentUser);

        return projectConverter.convertToDTO(project);
    }
}
