package WorkflowManager.project;

import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.Issue;
import WorkflowManager.project.model.CreateProjectRequest;
import WorkflowManager.project.model.ProjectDTO;
import WorkflowManager.project.model.UpdateProjectRequest;
import WorkflowManager.issue.IssueConverter;
import WorkflowManager.issue.IssueRepository;
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
        Project project = projectRepository.findByKey(key).orElseThrow(() -> new ProjectNotFoundException(key));
        return createProjectDataWithIssues(project);
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

    private ProjectDTO createProjectDataWithIssues(Project project) {
        ProjectDTO dto = projectConverter.convertToDTO(project);
        List<Issue> projectIssues = issueRepository.findFirstLevelIssuesOnProject(project.getId());
        dto.setIssueSummaries(projectIssues.stream().map(issueConverter::convertToSummaryDTO).toList());
        return dto;
    }
}
