package WorkflowManager.project.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Length(max = 50, min = 1)
    private String name;

    @NotBlank(message = "Project key is required")
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "Project key must be 2-10 uppercase letters")
    private String key;
    private String description;
    private Boolean isPrivate;

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }
}
