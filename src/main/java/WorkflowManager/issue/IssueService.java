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

    public List<Issue> getIssuesByProjectId(Long projectId) {
        return issueDAO.findByProjectId(projectId);
    }

    public Issue getIssueById(Long id) {
        return issueDAO.findById(id).orElseThrow(() -> new IssueNotFoundException(id));
    }

    public Issue getIssueByKey(String key) {
        return issueDAO.findByKey(key).orElseThrow(() -> new IssueNotFoundException(key));
    }

    @Transactional
    public Issue createIssue(CreateIssueInput input) {
        User currentUser = authContext.getCurrentUser();

        Long projectId = input.getProjectId();
        // Lock project row to safely increment
        Project project = projectDAO.findByIdForUpdate(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
        int nextIssueNumber = project.getIssueCounter() + 1;
        project.setIssueCounter(nextIssueNumber);

        Issue issue = issueConverter.convertFromInput(input);
        issue.setStatus(INITIAL_STATUS);
        issue.setProject(project);
        issue.setKey(project.getKey() + "-" + nextIssueNumber);

        if (input.getParentId() != null) {
            Issue parent = issueDAO.findById(input.getParentId()).orElseThrow();
            validateParent(issue, parent);
            issue.setParent(parent);
        }

        issue.setCreatedBy(currentUser);
        issue.setModifiedBy(currentUser);

        return issueDAO.save(issue);
    }

    @Transactional
    public Issue updateIssue(Long id, UpdateIssueInput input) {
        User currentUser = authContext.getCurrentUser();

        Issue issue = issueDAO.findById(id).orElseThrow(() -> new IssueNotFoundException(id));

        if (input.getTitle() != null) {
            issue.setTitle(input.getTitle());
        }

        if (input.getStatus() != null) {
            issue.setStatus(input.getStatus());
        }

        if (input.getAssignedUserId() != null) {
            User user = userDAO.findById(input.getAssignedUserId()).orElseThrow();
            issue.setAssigned(user);
        }

        if (input.getReporterUserId() != null) {
            User user = userDAO.findById(input.getReporterUserId()).orElseThrow();
            issue.setReporter(user);
        }

        issue.setModifiedBy(currentUser);

        return issue;
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
