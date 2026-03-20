package WorkflowManager.issue;

import WorkflowManager.exceptions.IssueNotFoundException;
import WorkflowManager.issue.dtos.CreateIssueDTO;
import WorkflowManager.issue.dtos.IssueDTO;
import WorkflowManager.issue.dtos.UpdateIssueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final IssueConverter issueConverter;

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

    public IssueDTO getIssueById(Long id) throws IssueNotFoundException {
        Issue issue = issueRepository.findById(id).orElseThrow(IssueNotFoundException::new);
        return issueConverter.convertToDTO(issue);
    }

    public IssueDTO createIssue(CreateIssueDTO issue) {
        Issue issueToSave = issueConverter.convertFromDTO(issue);
        issueToSave.setStatus("TODO");
        Issue savedIssue = issueRepository.save(issueToSave);
        return issueConverter.convertToDTO(savedIssue);
    }

    public IssueDTO updateIssue(Long id, UpdateIssueDTO issue) {
        Issue issueDb = issueRepository.findById(id).orElseThrow(IssueNotFoundException::new);

        if (issue.getTitle() != null) {
            issueDb.setTitle(issue.getTitle());
        }

        if (issue.getStatus() != null) {
            issueDb.setStatus(issue.getStatus());
        }

        Issue savedIssue = issueRepository.save(issueDb);
        return issueConverter.convertToDTO(savedIssue);
    }

    public void deleteIssue(Long id) {
        issueRepository.deleteById(id);
    }
}
