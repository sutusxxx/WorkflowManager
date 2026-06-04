package com.sutusxxx.graphql.project;

import com.sutusxxx.graphql.pagination.Cursor;
import com.sutusxxx.graphql.exceptions.NotFoundException;
import com.sutusxxx.graphql.issue.Status;
import com.sutusxxx.graphql.issue.model.CreateStatusInput;
import com.sutusxxx.graphql.project.model.CreateProjectInput;
import com.sutusxxx.graphql.project.model.UpdateProjectInput;
import com.sutusxxx.graphql.project.repository.ProjectRepository;
import graphql.relay.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final ProjectConverter projectConverter;

    private final MongoTemplate mongoTemplate;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectConverter projectConverter, MongoTemplate mongoTemplate) {
        this.projectRepository = projectRepository;
        this.projectConverter = projectConverter;
        this.mongoTemplate = mongoTemplate;
    }

    public Connection<Project> getProjects(Integer first, String after) {
        Query query = new Query();

        if (after != null) {
            Cursor cursor = Cursor.decode(after);
            query.addCriteria(Criteria.where("_id").gt(new ObjectId(cursor.id())));
        }

        query.limit(first + 1);
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        List<Project> results = mongoTemplate.find(query, Project.class);

        boolean hasNextPage = results.size() > first;
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

    public Project createProject(CreateProjectInput input) {
        Project project = projectConverter.convertFromInput(input);
        project.setVisibility(input.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);
        return projectRepository.save(project);
    }

    public Project updateProject(String id, UpdateProjectInput input) {
        Project project = projectRepository.findById(id).orElseThrow();

        if (input.description() != null && !input.description().equals(project.getDescription())) {
            project.setDescription(input.description());
        }

        project.setVisibility(input.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC);

        return project;
    }

    public Status addStatus(String projectId, CreateStatusInput input) {
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
}
