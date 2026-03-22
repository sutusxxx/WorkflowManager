package WorkflowManager.project;

import WorkflowManager.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.Issue;
import WorkflowManager.project.models.CreateProjectRequest;
import WorkflowManager.project.models.ProjectDTO;
import WorkflowManager.project.models.UpdateProjectRequest;
import WorkflowManager.issue.IssueConverter;
import WorkflowManager.issue.IssueRepository;
import WorkflowManager.issue.models.IssueDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final ProjectConverter projectConverter;
    private final IssueConverter issueConverter;

    public ProjectService(
            ProjectRepository projectRepository,
            IssueRepository issueRepository,
            ProjectConverter projectConverter,
            IssueConverter issueConverter
    ) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.projectConverter = projectConverter;
        this.issueConverter = issueConverter;
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream().map(this::createProjectDataWithIssues).toList();
    }

    public ProjectDTO getProjectByKey(String key) {
        Project project = projectRepository.findByKey(key).orElseThrow(ProjectNotFoundException::new);
        return createProjectDataWithIssues(project);
    }

    public ProjectDTO createProject(CreateProjectRequest project) {
        Project projectToSave = projectConverter.convertFromRequest(project);
        Project savedProject = projectRepository.save(projectToSave);
        return projectConverter.convertToDTO(savedProject);
    }

    public ProjectDTO updateProject(Long id, UpdateProjectRequest project) {
        Project projectDb = projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        if (project.getDescription() != null && !project.getDescription().equals(projectDb.getDescription())) {
            projectDb.setDescription(project.getDescription());
        }

        Project savedProject = projectRepository.save(projectDb);
        return projectConverter.convertToDTO(savedProject);
    }

    public List<IssueDTO> getIssuesByProject(Long projectId) {
        return issueRepository.findByProject(projectId).stream().map(issueConverter::convertToDTO).toList();
    }

    private ProjectDTO createProjectDataWithIssues(Project project) {
        ProjectDTO dto = projectConverter.convertToDTO(project);
        List<Issue> projectIssues = issueRepository.findFirstLevelIssuesOnProject(project.getId());
        dto.setIssueSummaries(projectIssues.stream().map(issueConverter::convertToSummaryDTO).toList());
        return dto;
    }
}
