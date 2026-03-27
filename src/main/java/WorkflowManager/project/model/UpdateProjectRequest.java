package WorkflowManager.project.model;

public class UpdateProjectRequest {
    private String name;
    private String description;
    private Boolean isPrivate;

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }
}
