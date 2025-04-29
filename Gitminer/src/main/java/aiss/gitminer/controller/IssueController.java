package aiss.gitminer.controller;

import aiss.gitminer.model.Issue;
import aiss.gitminer.repositories.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {
    @Autowired
    private IssueRepository issueRepository;

    @GetMapping
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @GetMapping("/{id}")
    public Issue getIssueById(@PathVariable String id) {
        Optional<Issue> issue = issueRepository.findById(id);
        return issue.orElse(null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Issue createIssue(@RequestBody Issue issue) {
        return issueRepository.save(issue);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteIssue(@PathVariable String id) {
        if (issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
        }
    }
}
