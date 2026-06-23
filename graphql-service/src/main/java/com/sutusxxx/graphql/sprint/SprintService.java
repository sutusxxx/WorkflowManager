package com.sutusxxx.graphql.sprint;

import com.sutusxxx.graphql.exceptions.BadRequestException;
import com.sutusxxx.graphql.exceptions.NotFoundException;
import com.sutusxxx.graphql.issue.Issue;
import com.sutusxxx.graphql.issue.repository.IssueRepository;
import com.sutusxxx.graphql.pagination.Page;
import com.sutusxxx.graphql.project.repository.ProjectRepository;
import com.sutusxxx.graphql.sprint.model.CreateSprintInput;
import com.sutusxxx.graphql.sprint.model.MoveIssueInput;
import com.sutusxxx.graphql.sprint.model.UpdateSprintInput;
import com.sutusxxx.graphql.sprint.repository.SprintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SprintService {
    private final IssueRepository issueRepository;
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

    public SprintService(
            IssueRepository issueRepository,
            SprintRepository sprintRepository,
            ProjectRepository projectRepository) {
        this.issueRepository = issueRepository;
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
    }

    public Sprint getActiveSprintByProjectId(String projectId) {
        return sprintRepository.findActiveByProjectId(projectId).orElseThrow(NotFoundException::new);
    }

    public Page<Sprint> getSprintsByProjectId(String projectId, Integer page, Integer pageSize) {
        List<Sprint> sprints = sprintRepository.findByProjectId(projectId);

        Map<SprintState, Integer> order = Map.of(
                SprintState.ACTIVE, 0,
                SprintState.OPEN, 1,
                SprintState.CLOSED, 2
        );

        sprints.sort(Comparator.comparingInt(s -> order.get(s.getState())));

        return new Page<>(
                sprints.stream()
                        .skip((long) page * pageSize)
                        .limit(pageSize)
                        .toList(),
                sprints.size(),
                page,
                pageSize
        );
    }

    public Sprint createSprint(String projectId, CreateSprintInput input) {
        projectRepository.findById(projectId).orElseThrow(NotFoundException::new);

        validateStartAndEndDate(input.startDate(), input.endDate());

        Sprint sprint = new Sprint();
        sprint.setProjectId(projectId);
        sprint.setName(input.name());
        sprint.setStartDate(input.startDate());
        sprint.setEndDate(input.endDate());
        sprint.setGoal(input.goal());
        sprint.setState(SprintState.OPEN);
        return sprintRepository.save(sprint);
    }

    public Sprint activate(String sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow(NotFoundException::new);
        if (sprint.getState() == SprintState.ACTIVE) {
            throw new BadRequestException("Sprint already active");
        }

        if (hasActiveByProject(sprint.getProjectId())) {
            throw new BadRequestException("Project already has an active sprint");
        }

        if (sprint.getStartDate() == null) sprint.setStartDate(Instant.now());

        sprint.setState(SprintState.ACTIVE);
        return sprintRepository.save(sprint);
    }

    public Sprint close(String sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow(NotFoundException::new);
        if (sprint.getState() == SprintState.CLOSED) {
            throw new BadRequestException("Sprint already active");
        }

        if (sprint.getState() == SprintState.OPEN) {
            throw new BadRequestException("Only active sprint can be closed");
        }

        sprint.setState(SprintState.CLOSED);
        return sprintRepository.save(sprint);
    }

    public Sprint updateSprint(String sprintId, UpdateSprintInput input) {
        validateStartAndEndDate(input.startDate(), input.endDate());

        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow(NotFoundException::new);
        sprint.setName(input.name());
        sprint.setGoal(input.goal());
        sprint.setStartDate(input.startDate());
        sprint.setEndDate(input.endDate());

        return sprintRepository.save(sprint);
    }

    @Transactional
    public Sprint moveIssue(String sprintId, MoveIssueInput input) {
        if (input.issueId().equals(input.nextIssueId())) {
            throw new BadRequestException("Issue cannot point to itself");
        }

        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow(NotFoundException::new);
        Issue issueToMove = issueRepository.findById(input.issueId()).orElseThrow(NotFoundException::new);

        boolean isInTargetSprint = sprint.getId().equals(issueToMove.getSprintId());

        if (isInTargetSprint) {
            issueRepository.findFirstBySprintIdAndNextIssueId(sprintId, input.issueId())
                    .ifPresent(prev -> {
                        prev.setNextIssueId(issueToMove.getNextIssueId());
                        issueRepository.save(prev);
                    });
        } else {
            if (issueToMove.getSprintId() != null) {
                issueRepository.findFirstBySprintIdAndNextIssueId(issueToMove.getSprintId(), input.issueId())
                        .ifPresent(prev -> {
                            prev.setNextIssueId(issueToMove.getNextIssueId());
                            issueRepository.save(prev);
                        });
            }
            issueToMove.setSprintId(sprintId);
            issueToMove.setNextIssueId(null);
        }

        issueRepository.findFirstBySprintIdAndNextIssueId(sprintId, input.nextIssueId())
                .ifPresent(prev -> {
                    prev.setNextIssueId(input.issueId());
                    issueRepository.save(prev);
                });

        issueToMove.setNextIssueId(input.nextIssueId());
        issueRepository.save(issueToMove);

        return sprint;
    }

    @Transactional
    public Sprint removeIssue(String issueId) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(NotFoundException::new);
        if (issue.getSprintId() == null) throw new BadRequestException("Issue is not in Sprint");

        Sprint sprint = sprintRepository.findById(issue.getSprintId()).orElseThrow(NotFoundException::new);

        issueRepository.findFirstBySprintIdAndNextIssueId(sprint.getId(), issueId).ifPresent(prev -> {
            prev.setNextIssueId(issue.getNextIssueId());
            issueRepository.save(prev);
        });

        issue.setSprintId(null);
        issue.setNextIssueId(null);
        issueRepository.save(issue);

        return sprint;
    }

    private void validateStartAndEndDate(Instant startDate, Instant endDate) {
        if (startDate == null && endDate != null) {
            throw new BadRequestException("End date must be provided when start date is not empty");
        }

        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            throw new BadRequestException("Start date must be before end date");
        }
    }

    private boolean hasActiveByProject(String projectId) {
        return sprintRepository.findActiveByProjectId(projectId).isPresent();
    }
}
