package com.sutusxxx.graphql.project;

import com.sutusxxx.graphql.exceptions.BadRequestException;
import com.sutusxxx.graphql.exceptions.PermissionDeniedException;
import com.sutusxxx.graphql.pagination.Cursor;
import com.sutusxxx.graphql.exceptions.NotFoundException;
import com.sutusxxx.graphql.issue.Status;
import com.sutusxxx.graphql.issue.model.CreateStatusInput;
import com.sutusxxx.graphql.permission.PermissionService;
import com.sutusxxx.graphql.project.model.AddMemberInput;
import com.sutusxxx.graphql.project.model.CreateProjectInput;
import com.sutusxxx.graphql.project.model.RemoveMemberInput;
import com.sutusxxx.graphql.project.model.UpdateProjectInput;
import com.sutusxxx.graphql.project.repository.ProjectRepository;
import com.sutusxxx.graphql.project.repository.RecentlyViewedRepository;
import com.sutusxxx.user.User;
import com.sutusxxx.user.repository.UserRepository;
import graphql.relay.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final RecentlyViewedRepository recentlyViewedRepository;
    private final UserRepository userRepository;

    private final PermissionService permissionService;

    private final ProjectConverter projectConverter;

    private final MongoTemplate mongoTemplate;

    public ProjectService(
            ProjectRepository projectRepository,
            RecentlyViewedRepository recentlyViewedRepository,
            UserRepository userRepository,
            PermissionService permissionService,
            ProjectConverter projectConverter,
            MongoTemplate mongoTemplate
    ) {
        this.projectRepository = projectRepository;
        this.recentlyViewedRepository = recentlyViewedRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.projectConverter = projectConverter;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Project> getRecentProjects(User currentUser, Integer limit) {
        List<String> recentProjectIds = getRecentProjectIds(currentUser.getId(), limit);

        List<String> filtered = recentProjectIds.stream()
                .filter(projectId ->
                        permissionService.hasPermission(currentUser, projectId, ProjectPermission.VIEW_PROJECT))
                .toList();

        return (!filtered.isEmpty())
                ? loadPreservingOrder(recentProjectIds)
                : List.of();
    }

    public Connection<Project> getProjects(User user, Integer first, String after) {
        Query query = new Query();

        if (after != null) {
            Cursor cursor = Cursor.decode(after);
            query.addCriteria(Criteria.where("_id").gt(cursor.id()));
        }

        query.addCriteria(new Criteria().orOperator(
                Criteria.where("members.userId").is(user.getId()),
                Criteria.where("createdBy").is(user.getId())
        ));

        query.limit(first + 1);
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        List<Project> results = mongoTemplate.find(query, Project.class);

        boolean hasNextPage = results.size() > first    ;
        List<Project> pageItems = hasNextPage ? results.subList(0, first) : results;

        List<Edge<Project>> edges = pageItems.stream()
                .map(p -> (Edge<Project>) new DefaultEdge<>(p, new DefaultConnectionCursor(
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

    public Project getProjectById(String id) {
        return projectRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Boolean trackView(User currentUser, String projectId) {
        RecentlyViewed recent = recentlyViewedRepository.findByUserIdAndProjectId(currentUser.getId(), projectId)
                .orElseGet(() -> {
                    RecentlyViewed recentNew = new RecentlyViewed();
                    recentNew.setUserId(currentUser.getId());
                    recentNew.setProjectId(projectId);
                    return recentNew;
                });
        recent.setLastViewed(Instant.now());
        recentlyViewedRepository.save(recent);
        return true;
    }

    @Transactional
    public Project assignProjectMember(User user, AddMemberInput input) {
        if (!permissionService.hasPermission(user, input.projectId(), ProjectPermission.MANAGE_MEMBERS)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        Project project = projectRepository.findById(input.projectId()).orElseThrow(NotFoundException::new);

        if (project.hasMember(input.userId())) {
            throw new BadRequestException("User already assigned to the project");
        }

        User userToAssign = userRepository.findById(input.userId()).orElseThrow(NotFoundException::new);

        List<ProjectMember> members = project.getMembers();
        members.add(addMember(userToAssign.getId(), input.role()));
        return projectRepository.save(project);
    }

    @Transactional
    public Project removeProjectMember(User user, RemoveMemberInput input) {
        if (!permissionService.hasPermission(user, input.projectId(), ProjectPermission.MANAGE_MEMBERS)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        Project project = projectRepository.findById(input.projectId()).orElseThrow(NotFoundException::new);

        if (!project.hasMember(input.userId())) {
            return project;
        }

        List<ProjectMember> members = project.getMembers();
        members.removeIf(member -> member.getUserId().equals(input.userId()));
        return projectRepository.save(project);
    }

    public List<String> getRecentProjectIds(String userId, int limit) {
        return recentlyViewedRepository
                    .findByUserIdOrderByLastViewedDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(RecentlyViewed::getProjectId)
                .toList();
    }

    public List<Project> loadPreservingOrder(List<String> projectIds) {
        if (projectIds.isEmpty()) return List.of();

        Map<String, Project> byId = projectRepository.findAllById(projectIds).stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        return projectIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull) // project may have been deleted since last view
                .toList();
    }

    public Project createProject(User user, CreateProjectInput input) {
        Project project = projectConverter.convertFromInput(input);
        project.setVisibility(input.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);
        return projectRepository.save(project);
    }

    public Project updateProject(User user, String id, UpdateProjectInput input) {
        if (!permissionService.hasPermission(user, id, ProjectPermission.EDIT_PROJECT)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        Project project = projectRepository.findById(id).orElseThrow();

        if (input.description() != null && !input.description().equals(project.getDescription())) {
            project.setDescription(input.description());
        }

        project.setVisibility(input.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);

        return project;
    }

    public Status addStatus(User user, String projectId, CreateStatusInput input) {
        if (!permissionService.hasPermission(user, projectId, ProjectPermission.MANAGE_STATUSES)) {
            throw new PermissionDeniedException("Action not allowed!");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        boolean nameExists = project.getStatuses().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(input.name()));
        if (nameExists) throw new RuntimeException("Already exists");

        if (input.isDefault()) {
            project.getStatuses().forEach(s -> s.setDefault(false));
        }

        Status newStatus = new Status();
        newStatus.setName(input.name());
        newStatus.setCategory(input.category());
        newStatus.setColor(input.color());
        newStatus.setDisplayOrder(input.displayOrder());
        newStatus.setDefault(input.isDefault());

        project.getStatuses().stream()
                .filter(status -> status.getDisplayOrder() >= newStatus.getDisplayOrder())
                .forEach(status -> status.setDisplayOrder(status.getDisplayOrder() + 1));

        project.getStatuses().add(newStatus);
        projectRepository.save(project);
        return newStatus;
    }

    public Project addTransition(String projectId, String fromStatusId, String toStatusId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundException::new);

        Status from = project.findStatusById(fromStatusId)
                .orElseThrow(() -> new RuntimeException("'From' status not found"));

        project.findStatusById(toStatusId)
                .orElseThrow(() -> new RuntimeException("'To' status not found"));

        if (!from.getAllowedTransitionIds().contains(toStatusId)) {
            from.getAllowedTransitionIds().add(toStatusId);
            projectRepository.save(project);
        }

        return project;
    }

    private ProjectMember addMember(String userId, ProjectRole role) {
        ProjectMember member = new ProjectMember();
        member.setRole(role);
        member.setUserId(userId);
        return member;
    }
}
