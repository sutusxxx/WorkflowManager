package WorkflowManager.issue;

import org.springframework.data.jpa.domain.Specification;

public class IssueSpecification {
    public static Specification<Issue> hasParent(Long parentId) {
        return (root, query, criteriaBuilder) -> parentId == null
                ? criteriaBuilder.isNull(root.get("parent"))
                : criteriaBuilder.equal(root.get("parent").get("id"), parentId);
    }

    public static Specification<Issue> hasProject(Long projectId) {
        return (root, query, criteriaBuilder) -> projectId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Issue> hasType(IssueType type) {
        return (root, query, criteriaBuilder) -> type == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("type"), type);
    }
}
