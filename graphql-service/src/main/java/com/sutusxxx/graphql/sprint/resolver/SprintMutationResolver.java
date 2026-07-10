package com.sutusxxx.graphql.sprint.resolver;

import com.sutusxxx.graphql.annotation.CurrentUser;
import com.sutusxxx.graphql.sprint.Sprint;
import com.sutusxxx.graphql.sprint.SprintService;
import com.sutusxxx.graphql.sprint.model.CreateSprintInput;
import com.sutusxxx.graphql.sprint.model.MoveIssueInput;
import com.sutusxxx.graphql.sprint.model.UpdateSprintInput;
import com.sutusxxx.user.User;
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
    public Sprint createSprint(@Argument String projectId, @Argument CreateSprintInput input, @CurrentUser User currentUser) {
        return sprintService.createSprint(currentUser, projectId, input);
    }

    @MutationMapping
    public Sprint updateSprint(@Argument String id, @Argument UpdateSprintInput input, @CurrentUser User currentUser) {
        return sprintService.updateSprint(currentUser, id, input);
    }

    @MutationMapping
    public Sprint activate(@Argument String id, @CurrentUser User currentUser) {
        return sprintService.activate(currentUser, id);
    }

    @MutationMapping
    public Sprint moveToSprint(@Argument String sprintId, @Argument MoveIssueInput input) {
        return sprintService.moveIssue(sprintId, input);
    }

    @MutationMapping
    public Sprint removeFromSprint(@Argument String issueId) {
        return sprintService.removeIssue(issueId);
    }
}
