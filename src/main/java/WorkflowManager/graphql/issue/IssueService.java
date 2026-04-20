package WorkflowManager.graphql.issue;

import WorkflowManager.common.exceptions.InvalidHierarchyException;
import WorkflowManager.common.exceptions.IssueNotFoundException;
import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.graphql.issue.model.CreateIssueInput;
import WorkflowManager.graphql.issue.model.IssueDTO;
import WorkflowManager.graphql.issue.model.UpdateIssueInput;
import WorkflowManager.graphql.issue.repository.IssueRepository;
import WorkflowManager.graphql.project.Project;
import WorkflowManager.graphql.project.repository.ProjectRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
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

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            IssueConverter issueConverter
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.issueConverter = issueConverter;
    }

    public List<IssueDTO> getIssuesByProjectId(String projectId) {
        return issueRepository.findByProjectId(projectId).stream().map(issueConverter::convertToDTO).toList();
    }

    public IssueDTO getIssueById(String id) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> new IssueNotFoundException(id));
        return issueConverter.convertToDTO(issue);
    }

    public IssueDTO getIssueByKey(String key) {
        Issue issue = issueRepository.findByKey(key).orElseThrow(() -> new IssueNotFoundException(key));
        return issueConverter.convertToDTO(issue);
    }

    public List<IssueDTO> getIssuesByParentId(String parentId) {
        return issueRepository.findByParentId(parentId).stream().map(issueConverter::convertToDTO).toList();
    }

    public IssueDTO createIssue(CreateIssueInput input, UserDetails user) {
        String projectId = input.projectId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Status defaultStatus = project.getDefaultStatus();

        int nextIssueNumber = project.getIssueCounter() + 1;
        project.setIssueCounter(nextIssueNumber);

        Issue issue = issueConverter.convertFromInput(input);
        issue.setStatusId(defaultStatus.getId());
        issue.setProjectId(project.getId());
        issue.setKey(project.getKey() + "-" + nextIssueNumber);

        if (input.parentId() != null) {
            Issue parent = issueRepository.findById(input.parentId()).orElseThrow();
            validateParent(issue, parent);
            issue.setParentID(parent.getId());
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueConverter.convertToDTO(savedIssue);
    }

    public IssueDTO updateIssue(String id, UpdateIssueInput input, UserDetails user) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> new IssueNotFoundException(id));

        if (input.title() != null) issue.setTitle(input.title());
        if (input.description() != null) issue.setDescription(input.description());
        if (input.storyPoints() != null) issue.setStoryPoints(input.storyPoints());
        if (input.dueDate() != null) issue.setDueDate(input.dueDate());

        return issueConverter.convertToDTO(issue);
    }

    public IssueDTO changeStatus(String issueId, String newStatusId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        Project project = projectRepository.findById(issue.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(issue.getProjectId()));

        Status currentStatus = project.findStatusById(issue.getStatusId())
                .orElseThrow();

        if (!currentStatus.getAllowedTransitionIds().contains(newStatusId)) {
            // TODO: error...
        }

        issue.setStatusId(newStatusId);
        issue.setUpdatedAt(LocalDateTime.now());
        Issue savedIssue = issueRepository.save(issue);
        return issueConverter.convertToDTO(savedIssue);
    }

    public void deleteIssue(String id) {
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
