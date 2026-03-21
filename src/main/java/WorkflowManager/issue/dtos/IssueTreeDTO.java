package WorkflowManager.issue.dtos;

import WorkflowManager.issue.IssueType;

import java.util.ArrayList;
import java.util.List;

public class IssueTreeDTO {
    private String key;
    private String title;
    private IssueType type;
    private String status;
    private Integer storyPoints;
    private List<IssueTreeDTO> children = new ArrayList<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public List<IssueTreeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<IssueTreeDTO> children) {
        this.children = children;
    }
}
