package WorkflowManager.issue;

import WorkflowManager.common.exceptions.InvalidHierarchyException;
import WorkflowManager.common.exceptions.IssueNotFoundException;
import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.model.CreateIssueRequest;
import WorkflowManager.issue.model.IssueDTO;
import WorkflowManager.issue.model.IssueTreeDTO;
import WorkflowManager.issue.model.UpdateIssueRequest;
import WorkflowManager.project.Project;
import WorkflowManager.project.ProjectRepository;
import WorkflowManager.user.User;
import WorkflowManager.user.UserRepository;
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
    private final UserRepository userRepository;

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
            UserRepository userRepository,
            IssueConverter issueConverter
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.issueConverter = issueConverter;
        this.userRepository = userRepository;
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
        Issue issue = issueRepository.findByKey(key).orElseThrow(() -> new IssueNotFoundException(key));
        return issueConverter.convertToTreeDTO(issue);
    }

    public IssueDTO getIssueByKey(String key) {
        Issue issue = issueRepository.findByKey(key).orElseThrow(() -> new IssueNotFoundException(key));
        return issueConverter.convertToDTO(issue);
    }

    public IssueDTO createIssue(CreateIssueRequest issueDTO) {
        String projectKey = issueDTO.getProjectKey();
        // Lock project row to safely increment
        Project project = projectRepository.findByKeyForUpdate(projectKey).orElseThrow(() -> new ProjectNotFoundException(projectKey));
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

    public IssueDTO updateIssue(String key, UpdateIssueRequest issueDTO) {
        Issue issue = issueRepository.findByKey(key).orElseThrow(() -> new IssueNotFoundException(key));

        if (issueDTO.getTitle() != null) {
            issue.setTitle(issueDTO.getTitle());
        }

        if (issueDTO.getStatus() != null) {
            issue.setStatus(issueDTO.getStatus());
        }

        if (issueDTO.getAssignedUserId() != null) {
            User user = userRepository.findById(issueDTO.getAssignedUserId()).orElseThrow();
            issue.setAssigned(user);
        }

        if (issueDTO.getReporterUserId() != null) {
            User user = userRepository.findById(issueDTO.getReporterUserId()).orElseThrow();
            issue.setReporter(user);
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueConverter.convertToDTO(savedIssue);
    }

    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    private void validateParent(Issue issue, Issue parent) {
        if (issue.getType() == IssueType.EPIC) {
            throw new InvalidHierarchyException("Epic cannot have parent");
        }

        Set<IssueType> allowed = ALLOWED_PARENTS.get(issue.getType());

        if (!allowed.contains(parent.getType())) {
            throw new InvalidHierarchyException(issue.getType() + " cannot be a child of " + parent.getType());
        }
    }
}
