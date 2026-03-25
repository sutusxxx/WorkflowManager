package WorkflowManager.project;

import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.Issue;
import WorkflowManager.issue.dao.IssueDAO;
import WorkflowManager.project.model.CreateProjectRequest;
import WorkflowManager.project.model.ProjectDTO;
import WorkflowManager.project.model.UpdateProjectRequest;
import WorkflowManager.issue.IssueConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final IssueDAO issueDAO;
    private final ProjectConverter projectConverter;
    private final IssueConverter issueConverter;

    public ProjectService(
            ProjectRepository projectRepository,
            IssueDAO issueDAO,
            ProjectConverter projectConverter,
            IssueConverter issueConverter
    ) {
        this.projectRepository = projectRepository;
        this.issueDAO = issueDAO;
        this.projectConverter = projectConverter;
        this.issueConverter = issueConverter;
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream().map(this::convertToProjectDTO).toList();
    }

    public ProjectDTO getProjectByKey(String key) {
        Project project = projectRepository.findByKey(key).orElseThrow(() -> new ProjectNotFoundException(key));
        return convertToProjectDTO(project);
    }

    public ProjectDTO createProject(CreateProjectRequest project) {
        Project projectToSave = projectConverter.convertFromRequest(project);
        Project savedProject = projectRepository.save(projectToSave);
        return projectConverter.convertToDTO(savedProject);
    }

    public ProjectDTO updateProject(String key, UpdateProjectRequest project) {
        Project projectDb = projectRepository.findByKey(key).orElseThrow(() -> new ProjectNotFoundException(key));

        if (project.getDescription() != null && !project.getDescription().equals(projectDb.getDescription())) {
            projectDb.setDescription(project.getDescription());
        }

        Project savedProject = projectRepository.save(projectDb);
        return projectConverter.convertToDTO(savedProject);
    }

    private ProjectDTO convertToProjectDTO(Project project) {
        ProjectDTO dto = projectConverter.convertToDTO(project);
        List<Issue> projectIssues = issueDAO.findFirstLevelByProjectId(project.getId());
        dto.setIssueSummaries(projectIssues.stream().map(issueConverter::convertToSummaryDTO).toList());
        return dto;
    }
}
