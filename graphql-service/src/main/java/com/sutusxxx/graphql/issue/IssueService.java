package com.sutusxxx.graphql.issue;

import com.sutusxxx.graphql.exceptions.BadRequestException;
import com.sutusxxx.graphql.exceptions.NotFoundException;
import com.sutusxxx.graphql.issue.model.AddIssueLinkInput;
import com.sutusxxx.graphql.issue.model.*;
import com.sutusxxx.graphql.issue.repository.IssueRepository;
import com.sutusxxx.graphql.project.Project;
import com.sutusxxx.graphql.project.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
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
        return issueRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public Issue getIssueByKey(String key) {
        return issueRepository.findByKey(key).orElseThrow(NotFoundException::new);
    }

    public List<Issue> getIssuesByParentId(String parentId) {
        return issueRepository.findByParentId(parentId);
    }

    public Issue createIssue(CreateIssueInput input) {
        String projectId = input.projectId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

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

        if (issue.getPriority() == null) {
            issue.setPriority(Priority.LOW);
        }

        return issueRepository.save(issue);
    }

    public Issue updateIssue(String id, UpdateIssueInput input) {
        Issue issue = issueRepository.findById(id).orElseThrow(NotFoundException::new);

        if (input.title() != null && !input.title().isEmpty()) issue.setTitle(input.title());
        if (input.description() != null) issue.setDescription(input.description());
        if (input.storyPoints() != null) issue.setStoryPoints(input.storyPoints());
        if (input.dueDate() != null) issue.setDueDate(input.dueDate());
        if (input.priority() != null) issue.setPriority(input.priority());

        return issueRepository.save(issue);
    }

    public Issue changeStatus(String issueId, String newStatusId) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(NotFoundException::new);

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
    public Boolean deleteIssue(String id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(NotFoundException::new);

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

        List<Issue> subIssues = issueRepository.findByParentId(id);
        issueRepository.deleteAll(subIssues);

        issueRepository.deleteById(id);
        return true;
    }

    public Issue addLink(AddIssueLinkInput input) {
        if (input.sourceIssueId().equals(input.targetIssueId())) {
            throw new RuntimeException("An issue cannot link to itself");
        }

        Issue source = issueRepository.findById(input.sourceIssueId())
                .orElseThrow(NotFoundException::new);

        Issue target = issueRepository.findById(input.targetIssueId())
                .orElseThrow(NotFoundException::new);

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

    public Issue removeLink(RemoveIssueLinkInput input) {
        Issue source = issueRepository.findById(input.sourceIssueId())
                .orElseThrow(NotFoundException::new);

        Issue target = issueRepository.findById(input.targetIssueId())
                .orElseThrow(NotFoundException::new);

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
}
