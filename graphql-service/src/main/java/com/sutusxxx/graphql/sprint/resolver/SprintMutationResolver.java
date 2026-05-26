package com.sutusxxx.graphql.sprint.resolver;

import com.sutusxxx.graphql.sprint.Sprint;
import com.sutusxxx.graphql.sprint.SprintService;
import com.sutusxxx.graphql.sprint.model.CreateSprintInput;
import com.sutusxxx.graphql.sprint.model.MoveIssueInput;
import com.sutusxxx.graphql.sprint.model.UpdateSprintInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class SprintMutationResolver {
    private final SprintService sprintService;

    public SprintMutationResolver(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @MutationMapping
    public Sprint createSprint(@Argument String projectId, @Argument CreateSprintInput input) {
        return sprintService.createSprint(projectId, input);
    }

    @MutationMapping
    public Sprint updateSprint(@Argument String id, @Argument UpdateSprintInput input) {
        return sprintService.updateSprint(id, input);
    }

    @MutationMapping
    public Sprint activate(@Argument String id) {
        return sprintService.activate(id);
    }

    @MutationMapping
    public Sprint moveIssue(@Argument String sprintId, @Argument MoveIssueInput input) {
        return sprintService.moveIssue(sprintId, input);
    }

    @MutationMapping
    public Sprint removeIssue(@Argument String issueId) {
        return sprintService.removeIssue(issueId);
    }
}
