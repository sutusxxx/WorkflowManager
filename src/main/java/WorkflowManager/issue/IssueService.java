package WorkflowManager.issue;

import WorkflowManager.exceptions.IssueNotFoundException;
import WorkflowManager.issue.dtos.CreateIssueDTO;
import WorkflowManager.issue.dtos.IssueDTO;
import WorkflowManager.issue.dtos.UpdateIssueDTO;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final IssueConverter issueConverter;

    private static final Map<IssueType, Set<IssueType>> ALLOWED_PARENTS = Map.of(
            IssueType.STORY, Set.of(IssueType.EPIC),
            IssueType.BUGFIX, Set.of(IssueType.EPIC),
            IssueType.TASK, Set.of(IssueType.EPIC),
            IssueType.SUB_TASK, Set.of(IssueType.STORY, IssueType.BUGFIX, IssueType.TASK),
            IssueType.EPIC, Set.of()
    );

    @Autowired
    public IssueService(
            IssueRepository issueRepository,
            IssueConverter issueConverter
    ) {
        this.issueRepository = issueRepository;
        this.issueConverter = issueConverter;
    }

    public List<IssueDTO> getAllIssues() {
        return issueRepository.findAll().stream().map(issueConverter::convertToDTO).toList();
    }

    public List<IssueDTO> getChildrenForIssue(Long issueId) throws NotImplementedException {
        throw new NotImplementedException("Not Implemented");
    }

    public IssueDTO getIssueById(Long id) throws IssueNotFoundException {
        Issue issue = issueRepository.findById(id).orElseThrow(IssueNotFoundException::new);
        return issueConverter.convertToDTO(issue);
    }

    public IssueDTO createIssue(CreateIssueDTO issueDTO) {
        Issue issue = issueConverter.convertFromDTO(issueDTO);

        issue.setStatus("TODO");

        if (issueDTO.getParentId() != null) {
            Issue parent = issueRepository.findById(issueDTO.getParentId()).orElseThrow();
            validateParent(issue, parent);
            issue.setParent(parent);
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueConverter.convertToDTO(savedIssue);
    }

    public IssueDTO updateIssue(Long id, UpdateIssueDTO issueDTO) {
        Issue issue = issueRepository.findById(id).orElseThrow(IssueNotFoundException::new);

        if (issueDTO.getTitle() != null) {
            issue.setTitle(issueDTO.getTitle());
        }

        if (issueDTO.getStatus() != null) {
            issue.setStatus(issueDTO.getStatus());
        }

        if (issueDTO.getParentId() != null) {
            Issue parent = issueRepository.findById(issueDTO.getParentId()).orElseThrow();
            validateParent(issue, parent);
            issue.setParent(parent);
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueConverter.convertToDTO(savedIssue);
    }

    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }

    private void validateParent(Issue issue, Issue parent) {
        if (issue.getType() == IssueType.EPIC) {
            throw new IllegalArgumentException("Epic cannot have parent");
        }

        Set<IssueType> allowed = ALLOWED_PARENTS.get(issue.getType());

        if (!allowed.contains(parent.getType())) {
            throw new IllegalArgumentException(issue.getType() + " cannot be a child of " + parent.getType());
        }
    }
}
