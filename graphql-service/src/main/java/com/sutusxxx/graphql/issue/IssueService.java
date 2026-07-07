package com.sutusxxx.graphql.issue;

import com.sutusxxx.graphql.audit.AuditService;
import com.sutusxxx.graphql.audit.model.AuditEvent;
import com.sutusxxx.graphql.exceptions.PermissionDeniedException;
import com.sutusxxx.graphql.pagination.Cursor;
import com.sutusxxx.graphql.exceptions.BadRequestException;
import com.sutusxxx.graphql.exceptions.NotFoundException;
import com.sutusxxx.graphql.issue.model.AddIssueLinkInput;
import com.sutusxxx.graphql.issue.model.*;
import com.sutusxxx.graphql.issue.repository.IssueRepository;
import com.sutusxxx.graphql.pagination.Page;
import com.sutusxxx.graphql.permission.PermissionService;
import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.ProjectPermission;
import com.sutusxxx.graphql.project.repository.ProjectRepository;
import com.sutusxxx.user.User;
import graphql.GraphQLException;
import graphql.relay.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;

    private final PermissionService permissionService;

    private final IssueConverter issueConverter;

    private final MongoTemplate mongoTemplate;

    private final AuditService audit;

    private static final Map<IssueType, Set<IssueType>> ALLOWED_PARENTS = Map.of(
            IssueType.STORY, Set.of(IssueType.EPIC),
            IssueType.BUGFIX, Set.of(IssueType.EPIC),
            IssueType.TASK, Set.of(IssueType.EPIC),
            IssueType.SUBTASK, Set.of(IssueType.STORY, IssueType.BUGFIX, IssueType.TASK),
            IssueType.EPIC, Set.of()
    );

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository, PermissionService permissionService,
            IssueConverter issueConverter, MongoTemplate mongoTemplate, AuditService audit
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.permissionService = permissionService;
        this.issueConverter = issueConverter;
        this.mongoTemplate = mongoTemplate;
        this.audit = audit;
    }

    public Connection<Issue> getBacklogIssuesByProjectId(String projectId, Integer first, String after) {
        Query query = new Query();

        query.addCriteria(Criteria.where("projectId").is(projectId));
        query.addCriteria(Criteria.where("sprintId").isNull());

        if (after != null) {
            Cursor cursor = Cursor.decode(after);
            query.addCriteria(Criteria.where("_id").gt(cursor.id()));
        }

        query.limit(first + 1);
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        List<Issue> results = mongoTemplate.find(query, Issue.class);

        boolean hasNextPage = results.size() > first;
        List<Issue> pageItems = hasNextPage ? results.subList(0, first) : results;

        List<Edge<Issue>> edges = pageItems.stream()
                .map(p -> (Edge<Issue>) new DefaultEdge<>(p, new DefaultConnectionCursor(
                        new Cursor(p.getId()).encode()
                )))
                .toList();

        PageInfo pageInfo = new DefaultPageInfo(
                edges.isEmpty() ? null : edges.get(0).getCursor(),
                edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor(),
                after != null,
                hasNextPage
        );
        return new DefaultConnection<>(edges, pageInfo);
    }

    public List<Issue> getIssuesBySprintId(String sprintId) {
        List<Issue> issues = issueRepository.findBySprintId(sprintId);

        if (issues.isEmpty()) return Collections.emptyList();
        if (issues.size() == 1) return issues;

        Map<String, Issue> byNextId = new HashMap<>();
        for (Issue issue : issues) {
            String key = issue.getNextIssueId(); // null key = tail node
            if (byNextId.containsKey(key)) {
                throw new IllegalStateException("Broken chain: two issues share nextIssueId=" + key);
            }
            byNextId.put(key, issue);
        }

        Issue tail = byNextId.get(null);
        if (tail == null) {
            throw new IllegalStateException("Cycle detected — no tail found in sprint: " + sprintId);
        }

        List<Issue> result = new ArrayList<>(issues.size());
        Set<String> visited = new HashSet<>();
        Issue current = tail;

        while (current != null) {
            if (!visited.add(current.getId())) {
                throw new IllegalStateException("Cycle detected at issue: " + current.getId());
            }
            result.add(current);
            current = byNextId.get(current.getId());
        }

        if (result.size() != issues.size()) {
            throw new IllegalStateException(
                    "Disconnected issues in sprint " + sprintId +
                            " — expected " + issues.size() + ", walked " + result.size()
            );
        }

        Collections.reverse(result);
        return result;
    }

    public Page<Issue> getIssuesByProjectId(String projectId, Integer page, Integer pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId));
        query.with(Sort.by((Sort.Direction.DESC), "createdAt"));

        long total = mongoTemplate.count(query, Issue.class);

        query.skip((long) page * pageSize);
        query.limit(pageSize);

        List<Issue> items = mongoTemplate.find(query, Issue.class);

        return new Page<>(items, total, page, pageSize);
    }

    public Issue getIssueByKey(String key) {
        return issueRepository.findByKey(key).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Issue createIssue(User user, CreateIssueInput input) {
        String projectId = input.projectId();

        if (!permissionService.hasPermission(user, projectId, ProjectPermission.CREATE_ISSUE)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        int nextIssueNumber = project.getIssueCounter() + 1;
        project.setIssueCounter(nextIssueNumber);

        projectRepository.save(project);

        Issue issue = issueConverter.convertFromInput(input);
        issue.setStatusId(project.getDefaultStatus().getId());
        issue.setProjectId(project.getId());
        issue.setKey(project.getKey() + "-" + nextIssueNumber);
        issue.setSprintId(input.sprintId());

        if (input.parentId() != null) {
            Issue parent = issueRepository.findById(input.parentId()).orElseThrow(NotFoundException::new);
            validateParent(issue, parent);
            issue.setParentId(parent.getId());
        }

        if (issue.getPriority() == null) {
            issue.setPriority(Priority.LOW);
        }

        Issue savedIssue = issueRepository.save(issue);

        if (savedIssue.getSprintId() != null) {
            appendToTail(savedIssue);
        }

        return savedIssue;
    }

    public Issue updateIssue(User user, String id, UpdateIssueInput input) {
        Issue issue = issueRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!permissionService.hasPermission(user, issue.getProjectId(), ProjectPermission.EDIT_ISSUE)) {
            throw new PermissionDeniedException("Action not allowed!"); 
        }

        if (input.title() != null && !input.title().isEmpty()) issue.setTitle(input.title());
        if (input.description() != null) issue.setDescription(input.description());
        if (input.storyPoints() != null) issue.setStoryPoints(input.storyPoints());
        if (input.dueDate() != null) issue.setDueDate(input.dueDate());
        if (input.priority() != null) issue.setPriority(input.priority());

        return issueRepository.save(issue);
    }

    @Transactional
    public Issue changeStatus(User user, String issueId, String newStatusId) {
        audit.log(new AuditEvent(
                "STATUS_CHANGE",
                "Issue",
                issueId
        ));

        Issue issue = issueRepository.findById(issueId).orElseThrow(NotFoundException::new);

        if (!permissionService.hasPermission(user, issue.getProjectId(), ProjectPermission.TRANSITION_ISSUE)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        Project project = projectRepository.findById(issue.getProjectId())
                .orElseThrow(NotFoundException::new);

        Status currentStatus = project.findStatusById(issue.getStatusId())
                .orElseThrow();

        Status newStatus = project.findStatusById(newStatusId)
                .orElseThrow();

        if (!currentStatus.getAllowedTransitionIds().contains(newStatusId)) {
            throw new BadRequestException(
                    "Transition from {} to {} is not allowed", currentStatus.getName(), newStatus.getName());
        }

        issue.setStatusId(newStatusId);
        return issueRepository.save(issue);
    }

    @Transactional
    public String deleteIssue(User user, String id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (permissionService.hasPermission(user, issue.getProjectId(), ProjectPermission.DELETE_ISSUE)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        Set<String> targetIds = issue.getLinks().stream()
                .map(IssueLink::getTargetIssueId)
                .collect(Collectors.toSet());

        if (!targetIds.isEmpty()) {
            Map<String, Issue> targets = issueRepository.findAllById(targetIds)
                    .stream()
                    .collect(Collectors.toMap(Issue::getId, i -> i));

            for (IssueLink link : issue.getLinks()) {
                Issue target = targets.get(link.getTargetIssueId());
                if (target == null) continue;

                link.getLinkType().inverse().ifPresent(inverseType ->
                        target.removeLink(issue.getId(), inverseType));

                if (link.getLinkType().isSymmetric()) {
                    target.removeLink(issue.getId(), IssueLinkType.RELATES_TO);
                }
            }

            issueRepository.saveAll(targets.values());
        }

        detachFromChain(issue);

        List<Issue> subIssues = issueRepository.findByParentId(id);

        subIssues.forEach(this::detachFromChain);

        issueRepository.deleteAll(subIssues);

        issueRepository.deleteById(id);
        return id;
    }

    public Issue addLink(User user, AddIssueLinkInput input) {
        if (input.sourceIssueId().equals(input.targetIssueId())) {
            throw new GraphQLException("An issue cannot link to itself");
        }

        Issue source = issueRepository.findById(input.sourceIssueId())
                .orElseThrow(NotFoundException::new);

        Issue target = issueRepository.findById(input.targetIssueId())
                .orElseThrow(NotFoundException::new);

        if (!source.getProjectId().equals(target.getProjectId())) {
            throw new GraphQLException("Cannot link issue");
        }

        if (permissionService.hasPermission(user, source.getProjectId(), ProjectPermission.EDIT_ISSUE)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        if (source.hasLinkTo(target.getId(), input.linkType())) {
            throw new BadRequestException("Link already exists");
        }

        IssueLink forwardLink = new IssueLink(
                target.getId(),
                input.linkType(),
                OffsetDateTime.now()
        );
        source.getLinks().add(forwardLink);
        issueRepository.save(source);

        input.linkType().inverse().ifPresent(inverseType -> {
            if (!target.hasLinkTo(source.getId(), inverseType)) {
                IssueLink inverseLink = new IssueLink(
                        source.getId(),
                        inverseType,
                        OffsetDateTime.now()
                );
                target.getLinks().add(inverseLink);
                issueRepository.save(target);
            }
        });

        if (input.linkType().isSymmetric()
                && !target.hasLinkTo(source.getId(), IssueLinkType.RELATES_TO)) {
            IssueLink symmetricLink = new IssueLink(
                    source.getId(),
                    IssueLinkType.RELATES_TO,
                    OffsetDateTime.now()
            );
            target.getLinks().add(symmetricLink);
            issueRepository.save(target);
        }

        return source;
    }

    public Issue removeLink(User user, RemoveIssueLinkInput input) {
        Issue source = issueRepository.findById(input.sourceIssueId())
                .orElseThrow(NotFoundException::new);

        Issue target = issueRepository.findById(input.targetIssueId())
                .orElseThrow(NotFoundException::new);

        if (permissionService.hasPermission(user, source.getProjectId(), ProjectPermission.EDIT_ISSUE)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        source.removeLink(target.getId(), input.linkType());
        issueRepository.save(source);

        input.linkType().inverse().ifPresent(inverseType -> {
            target.removeLink(source.getId(), inverseType);
            issueRepository.save(target);
        });

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
                .findByParentIdIn(parentIds)
                .stream()
                .collect(Collectors.groupingBy(Issue::getParentId));

        return issues.stream().collect(Collectors.toMap(
                Function.identity(),
                i -> grouped.getOrDefault(i.getId(), List.of())
        ));
    }

    public Map<Issue, List<IssueLinkDTO>> loadLinks(List<Issue> issues) {
        Set<String> targetIds = issues.stream()
                .map(Issue::getLinks)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(IssueLink::getTargetIssueId)
                .collect(Collectors.toSet());

        Map<String, Issue> targetsById = issueRepository.findAllById(targetIds)
                .stream()
                .collect(Collectors.toMap(Issue::getId, Function.identity()));

        return issues.stream().collect(Collectors.toMap(
                Function.identity(),
                issue -> issue.getLinks().stream().map(link -> {
                    IssueLinkDTO dto = new IssueLinkDTO();
                    dto.setSource(issue);
                    dto.setTarget(targetsById.get(link.getTargetIssueId()));
                    dto.setLinkType(link.getLinkType());
                    return dto;
                }).filter(link -> link.getTarget() != null).toList()
        ));
    }

    public Map<Issue, Issue> batchLoadParents(List<Issue> issues) {
        Set<String> parentIds = issues.stream()
                .map(Issue::getParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Issue, Issue> result = new HashMap<>();

        if (parentIds.isEmpty()) return result;

        Map<String, Issue> parentsById = issueRepository.findAllById(parentIds)
                .stream()
                .collect(Collectors.toMap(Issue::getId, Function.identity()));

        return issues.stream().collect(Collectors.toMap(
                Function.identity(),
                issue -> parentsById.get(issue.getParentId())
        ));
    }

    public Map<Issue, Status> batchLoadStatuses(List<Issue> issues) {
        Set<String> projectIds = issues.stream()
                .map(Issue::getProjectId)
                .collect(Collectors.toSet());

        Map<String, List<Status>> statusesByProjectId = projectRepository.findAllById(projectIds)
                .stream()
                .collect(Collectors.toMap(Project::getId, Project::getStatuses));

        return issues.stream().collect(Collectors.toMap(
                Function.identity(),
                issue -> {
                    List<Status> projectStatuses = statusesByProjectId.get(issue.getProjectId());
                    return projectStatuses.stream()
                            .filter(s -> Objects.equals(s.getId(), issue.getStatusId()))
                            .findFirst()
                            .orElseThrow(NotFoundException::new);
                }
        ));
    }

    public Map<Issue, Project> batchLoadProjects(List<Issue> issues) {
        Set<String> projectIds = issues.stream()
                .map(Issue::getProjectId)
                .collect(Collectors.toSet());
        Map<String, Project> projectsById = projectRepository.findAllById(projectIds)
                .stream()
                .collect(Collectors.toMap(Project::getId, Function.identity()));

        return issues.stream().collect(Collectors.toMap(
                Function.identity(),
                issue -> projectsById.get(issue.getProjectId())
        ));
    }

    private void validateParent(Issue issue, Issue parent) {
        if (issue.getType() == IssueType.EPIC) {
            throw new BadRequestException("Epic cannot have parent");
        }

        Set<IssueType> allowed = ALLOWED_PARENTS.get(issue.getType());

        if (!allowed.contains(parent.getType())) {
            throw new BadRequestException(issue.getType() + " cannot be a child of " + parent.getType());
        }
    }

    private void appendToTail(Issue issue) {
        issueRepository.findFirstBySprintIdAndNextIssueIdIsNull(issue.getSprintId())
                .filter(tail -> !tail.getId().equals(issue.getId()))
                .ifPresent(tail -> {
                    tail.setNextIssueId(issue.getId());
                    issueRepository.save(tail);
                });
    }

    private void detachFromChain(Issue issue) {
        if (issue.getSprintId() != null) {
            issueRepository.findFirstBySprintIdAndNextIssueId(issue.getSprintId(), issue.getId())
                    .ifPresent(prev -> {
                        prev.setNextIssueId(issue.getNextIssueId());
                        issueRepository.save(prev);
                    });
        } else {
            issueRepository.findFirstByProjectIdAndSprintIdIsNullAndNextIssueId(issue.getProjectId(), issue.getId())
                    .ifPresent(prev -> {
                        prev.setNextIssueId(issue.getNextIssueId());
                        issueRepository.save(prev);
                    });
        }
    }
}
