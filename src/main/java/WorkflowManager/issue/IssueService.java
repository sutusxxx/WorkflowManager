package WorkflowManager.issue;

import WorkflowManager.common.exceptions.InvalidHierarchyException;
import WorkflowManager.common.exceptions.IssueNotFoundException;
import WorkflowManager.common.exceptions.ProjectNotFoundException;
import WorkflowManager.issue.model.*;
import WorkflowManager.issue.repository.IssueRepository;
import WorkflowManager.project.Project;
import WorkflowManager.project.repository.ProjectRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Issue> getIssuesByProjectId(String projectId) {
        return issueRepository.findByProjectId(projectId);
    }

    public Issue getIssueById(String id) {
        return issueRepository.findById(id).orElseThrow(() -> new IssueNotFoundException(id));
    }

    public Issue getIssueByKey(String key) {
        return issueRepository.findByKey(key).orElseThrow(() -> new IssueNotFoundException(key));
    }

    public List<Issue> getIssuesByParentId(String parentId) {
        return issueRepository.findByParentId(parentId);
    }

    public Issue createIssue(CreateIssueInput input, UserDetails user) {
        String projectId = input.projectId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        int nextIssueNumber = project.getIssueCounter() + 1;
        project.setIssueCounter(nextIssueNumber);

        Issue issue = issueConverter.convertFromInput(input);
        issue.setStatusId(project.getDefaultStatus().getId());
        issue.setProjectId(project.getId());
        issue.setKey(project.getKey() + "-" + nextIssueNumber);

        if (input.parentId() != null) {
            Issue parent = issueRepository.findById(input.parentId()).orElseThrow();
            validateParent(issue, parent);
            issue.setParentId(parent.getId());
        }

        return issueRepository.save(issue);
    }

    public Issue updateIssue(String id, UpdateIssueInput input, UserDetails user) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> new IssueNotFoundException(id));

        if (input.title() != null) issue.setTitle(input.title());
        if (input.description() != null) issue.setDescription(input.description());
        if (input.storyPoints() != null) issue.setStoryPoints(input.storyPoints());
        if (input.dueDate() != null) issue.setDueDate(input.dueDate());

        return issueRepository.save(issue);
    }

    public Issue changeStatus(String issueId, String newStatusId) {
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
        return issueRepository.save(issue);
    }

    public void deleteIssue(String id) {
        issueRepository.deleteById(id);
    }

    public Issue addLink(AddIssueLinkInput input) {
        if (input.sourceIssueId().equals(input.targetIssueId())) {
            throw new RuntimeException("An issue cannot link to itself");
        }

        Issue source = issueRepository.findById(input.sourceIssueId())
                .orElseThrow(() -> new RuntimeException("Source issue not found"));

        Issue target = issueRepository.findById(input.targetIssueId())
                .orElseThrow(() -> new RuntimeException("Target issue not found"));

        // ── Guard: duplicate link ────────────────────────────────
        if (source.hasLinkTo(target.getId(), input.linkType())) {
            throw new RuntimeException("Link already exists");
        }

        // ── Add link to source ───────────────────────────────────
        IssueLink forwardLink = new IssueLink(
                target.getId(),
                input.linkType(),
                LocalDateTime.now(),
                input.createdBy()
        );
        source.getLinks().add(forwardLink);
        issueRepository.save(source);

        // ── Auto-create inverse link on target ───────────────────
        input.linkType().inverse().ifPresent(inverseType -> {
            if (!target.hasLinkTo(source.getId(), inverseType)) {
                IssueLink inverseLink = new IssueLink(
                        source.getId(),
                        inverseType,
                        LocalDateTime.now(),
                        input.createdBy()
                );
                target.getLinks().add(inverseLink);
                issueRepository.save(target);
            }
        });

        // For RELATES_TO (symmetric): also add on the target side
        if (input.linkType().isSymmetric()
                && !target.hasLinkTo(source.getId(), IssueLinkType.RELATES_TO)) {
            IssueLink symmetricLink = new IssueLink(
                    source.getId(),
                    IssueLinkType.RELATES_TO,
                    LocalDateTime.now(),
                    input.createdBy()
            );
            target.getLinks().add(symmetricLink);
            issueRepository.save(target);
        }

        return source;
    }

    public Issue removeLink(RemoveIssueLinkInput input) {
        Issue source = issueRepository.findById(input.sourceIssueId())
                .orElseThrow(() -> new RuntimeException("Source issue not found"));

        Issue target = issueRepository.findById(input.targetIssueId())
                .orElseThrow(() -> new RuntimeException("Target issue not found"));

        // ── Remove from source ───────────────────────────────────
        source.removeLink(target.getId(), input.linkType());
        issueRepository.save(source);

        // ── Remove inverse from target ───────────────────────────
        input.linkType().inverse().ifPresent(inverseType -> {
            target.removeLink(source.getId(), inverseType);
            issueRepository.save(target);
        });

        // For RELATES_TO: remove both sides
        if (input.linkType().isSymmetric()) {
            target.removeLink(source.getId(), IssueLinkType.RELATES_TO);
            issueRepository.save(target);
        }

        return source;
    }

    public Map<Issue, List<Issue>> loadChildren(List<Issue> issues) {
        Set<String> parentIds = issues.stream()
                .map(Issue::getId)
                .collect(Collectors.toSet());

        Map<String, List<Issue>> grouped = issueRepository
                .findByParentIdIn(parentIds)       // single DB call
                .stream()
                .collect(Collectors.groupingBy(Issue::getParentId));

        return issues.stream().collect(Collectors.toMap(
                i -> i,
                i -> grouped.getOrDefault(i.getId(), List.of())
        ));
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
