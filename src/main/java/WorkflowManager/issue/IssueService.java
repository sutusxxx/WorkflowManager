package WorkflowManager.issue;

import WorkflowManager.auth.AuthContext;
import WorkflowManager.common.exceptions.InvalidHierarchyException;
import WorkflowManager.common.exceptions.IssueNotFoundException;
import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.dao.IssueDAO;
import WorkflowManager.issue.model.*;
import WorkflowManager.project.Project;
import WorkflowManager.project.dao.ProjectDAO;
import WorkflowManager.user.User;
import WorkflowManager.user.dao.UserDAO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class IssueService {
    private final IssueDAO issueDAO;
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;

    private final IssueConverter issueConverter;

    private final AuthContext authContext;

    private static final Map<IssueType, Set<IssueType>> ALLOWED_PARENTS = Map.of(
            IssueType.STORY, Set.of(IssueType.EPIC),
            IssueType.BUGFIX, Set.of(IssueType.EPIC),
            IssueType.TASK, Set.of(IssueType.EPIC),
            IssueType.SUBTASK, Set.of(IssueType.STORY, IssueType.BUGFIX, IssueType.TASK),
            IssueType.EPIC, Set.of()
    );

    private static final String INITIAL_STATUS = "Todo";

    public IssueService(
            IssueDAO issueDAO,
            ProjectDAO projectDAO,
            UserDAO userDAO,
            IssueConverter issueConverter,
            AuthContext authContext
    ) {
        this.issueDAO = issueDAO;
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.issueConverter = issueConverter;
        this.authContext = authContext;
    }

    public List<IssueDTO> getAllIssues(Long parentId, Long projectId, IssueType type, String status) {
        Specification<Issue> spec = Specification
                .where(IssueSpecification.hasProject(projectId))
                .and(IssueSpecification.hasType(type));

        if (parentId != null) {
            spec = spec.and(IssueSpecification.hasParent(parentId));
        }

        return issueDAO.findAll().stream().map(issueConverter::convertToDTO).toList();
    }

    public List<IssueSummaryDTO> getIssuesByProjectId(Long projectId) {
        List<Issue> issues = issueDAO.findByProjectId(projectId);
        return issues.stream().map(issueConverter::convertToSummaryDTO).toList();
    }

    public IssueTreeDTO getTree(Long id) {
        Issue issue = issueDAO.findById(id).orElseThrow(() -> new IssueNotFoundException(id));
        return issueConverter.convertToTreeDTO(issue);
    }

    public IssueDTO getIssueById(Long id) {
        Issue issue = issueDAO.findById(id).orElseThrow(() -> new IssueNotFoundException(id));
        return issueConverter.convertToDTO(issue);
    }

    public IssueDTO getIssueByKey(String key) {
        Issue issue = issueDAO.findByKey(key).orElseThrow(() -> new IssueNotFoundException(key));
        return issueConverter.convertToDTO(issue);
    }

    @Transactional
    public IssueDTO createIssue(CreateIssueRequest request) {
        User currentUser = authContext.getCurrentUser();

        Long projectId = request.getProjectId();
        // Lock project row to safely increment
        Project project = projectDAO.findByIdForUpdate(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
        int nextIssueNumber = project.getIssueCounter() + 1;
        project.setIssueCounter(nextIssueNumber);

        Issue issue = issueConverter.convertFromRequest(request);
        issue.setStatus(INITIAL_STATUS);
        issue.setProject(project);
        issue.setKey(project.getKey() + "-" + nextIssueNumber);

        if (request.getParentId() != null) {
            Issue parent = issueDAO.findById(request.getParentId()).orElseThrow();
            validateParent(issue, parent);
            issue.setParent(parent);
        }

        issue.setCreatedBy(currentUser);

        Issue savedIssue = issueDAO.save(issue);

        return issueConverter.convertToDTO(savedIssue);
    }

    @Transactional
    public IssueDTO updateIssue(Long id, UpdateIssueRequest issueDTO) {
        User currentUser = authContext.getCurrentUser();

        Issue issue = issueDAO.findById(id).orElseThrow(() -> new IssueNotFoundException(id));

        if (issueDTO.getTitle() != null) {
            issue.setTitle(issueDTO.getTitle());
        }

        if (issueDTO.getStatus() != null) {
            issue.setStatus(issueDTO.getStatus());
        }

        if (issueDTO.getAssignedUserId() != null) {
            User user = userDAO.findById(issueDTO.getAssignedUserId()).orElseThrow();
            issue.setAssigned(user);
        }

        if (issueDTO.getReporterUserId() != null) {
            User user = userDAO.findById(issueDTO.getReporterUserId()).orElseThrow();
            issue.setReporter(user);
        }

        issue.setModifiedBy(currentUser);

        return issueConverter.convertToDTO(issue);
    }

    @Transactional
    public void deleteIssue(Long id) {
        issueDAO.deleteById(id);
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
