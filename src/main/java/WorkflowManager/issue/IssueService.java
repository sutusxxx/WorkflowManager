package WorkflowManager.issue;

import WorkflowManager.exceptions.IssueNotFoundException;
import WorkflowManager.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.models.CreateIssueRequest;
import WorkflowManager.issue.models.IssueDTO;
import WorkflowManager.issue.models.IssueTreeDTO;
import WorkflowManager.issue.models.UpdateIssueRequest;
import WorkflowManager.project.Project;
import WorkflowManager.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;

    private final IssueConverter issueConverter;

    private static final Map<IssueType, Set<IssueType>> ALLOWED_PARENTS = Map.of(
            IssueType.STORY, Set.of(IssueType.EPIC),
            IssueType.BUGFIX, Set.of(IssueType.EPIC),
            IssueType.TASK, Set.of(IssueType.EPIC),
            IssueType.SUBTASK, Set.of(IssueType.STORY, IssueType.BUGFIX, IssueType.TASK),
            IssueType.EPIC, Set.of()
    );

    @Autowired
    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            IssueConverter issueConverter
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.issueConverter = issueConverter;
    }

    public List<IssueDTO> getAllIssues(Long parentId, Long projectId, IssueType type, String status) {
        Specification<Issue> spec = Specification
                .where(IssueSpecification.hasProject(projectId))
                .and(IssueSpecification.hasType(type));

        if (parentId != null) {
            spec = spec.and(IssueSpecification.hasParent(parentId));
        }

        return issueRepository.findAll(spec).stream().map(issueConverter::convertToDTO).toList();
    }

    public IssueTreeDTO getTree(String key) {
        Issue issue = issueRepository.findByKey(key).orElseThrow(IssueNotFoundException::new);
        return issueConverter.convertToTreeDTO(issue);
    }

    public List<IssueDTO> getChildren(Long parentId) {
        return issueRepository.findAllByParent(parentId).stream().map(issueConverter::convertToDTO).toList();
    }

    public IssueDTO getIssueByKey(String key) throws IssueNotFoundException {
        Issue issue = issueRepository.findByKey(key).orElseThrow(IssueNotFoundException::new);
        return issueConverter.convertToDTO(issue);
    }

    public IssueDTO createIssue(CreateIssueRequest issueDTO) {
        // Lock project row to safely increment
        Project project = projectRepository.findByKeyForUpdate(issueDTO.getProjectKey()).orElseThrow(ProjectNotFoundException::new);
        int nextIssueNumber = project.getIssueCounter() + 1;
        project.setIssueCounter(nextIssueNumber);
        projectRepository.save(project);

        Issue issue = issueConverter.convertFromRequest(issueDTO);
        issue.setStatus(Issue.INITIAL_STATUS);
        issue.setProject(project);
        issue.setKey(project.getKey() + "-" + nextIssueNumber);

        if (issueDTO.getParentKey() != null) {
            Issue parent = issueRepository.findByKey(issueDTO.getParentKey()).orElseThrow();
            validateParent(issue, parent);
            issue.setParent(parent);
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueConverter.convertToDTO(savedIssue);
    }

    public IssueDTO updateIssue(Long id, UpdateIssueRequest issueDTO) {
        Issue issue = issueRepository.findById(id).orElseThrow(IssueNotFoundException::new);

        if (issueDTO.getTitle() != null) {
            issue.setTitle(issueDTO.getTitle());
        }

        if (issueDTO.getStatus() != null) {
            issue.setStatus(issueDTO.getStatus());
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueConverter.convertToDTO(savedIssue);
    }

    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    private void validateParent(Issue issue, Issue parent) {
        if (issue.getType() == IssueType.EPIC) {
            throw new IllegalArgumentException("Epic cannot have parent");
        }

        Set<IssueType> allowed = ALLOWED_PARENTS.get(issue.getType());

        if (!allowed.contains(parent.getType())) {
            throw new IllegalArgumentException(issue.getType() + " cannot be a child of " + parent.getType());
        }
    }
}
