package aiss.gitminer.controller;

import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer")
public class IssueController {
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ProjectRepository projectRepository;

    // GET http://localhost:8080/gitminer/issues
    @GetMapping("/issues")
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/issues/{id}
    @GetMapping("/issues/{id}")
    public Issue getIssueById(@PathVariable String id) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(id);
        if(!issue.isPresent()){
            throw new IssueNotFoundException();
        }
        return issue.get();
    }

    // GET http://localhost:8080/gitminer/projects/{projectId}/issues
    @GetMapping("/projects/{projectId}/issues")
    public List<Issue> getAllIssuesByProjectId(@PathVariable(value="projectId") String projectId) throws ProjectNotFoundException {
        Optional<Project> project = projectRepository.findById(projectId);
        if(!project.isPresent()){
            throw new ProjectNotFoundException();
        }
        List<Issue> issues = new ArrayList<>(project.get().getIssues());
        return issues;
    }

    // POST http://localhost:8080/gitminer/projects/{projectId}/issues
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/projects/{projectId}/issues")
    public Issue createIssue(@RequestBody @Valid Issue issue, @PathVariable String projectId) throws ProjectNotFoundException {
        Optional <Project> project = projectRepository.findById(projectId);
        if(!project.isPresent()){
            throw new ProjectNotFoundException();
        }
        Issue newIssue = new Issue(
                issue.getTitle(),
                issue.getDescription(),
                issue.getState(),
                issue.getLabels(),
                issue.getAuthor(),
                issue.getComments()
        );
        project.get().getIssues().add(newIssue);
        projectRepository.save(project.get());
        return newIssue;
    }

    // DELETE http://localhost:8080/gitminer/issues/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/issues/{id}")
    public void deleteIssue(@PathVariable String id) throws IssueNotFoundException {
        if (issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
        }else{
            throw new IssueNotFoundException();
        }
    }

    // PUT http://localhost:8080/gitminer/issues/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/issues/{id}")
    public void updateIssue(@RequestBody @Valid Issue issue, @PathVariable String id) throws IssueNotFoundException {
        Optional <Issue> issueData = issueRepository.findById(id);
        if(!issueData.isPresent()){
            throw new IssueNotFoundException();
        }
        Issue _issue = issueData.get();
        _issue.setState(issue.getState());
        _issue.setLabels(issue.getLabels());
        _issue.setDescription(issue.getDescription());
        _issue.setTitle(issue.getTitle());
        _issue.setVotes(issue.getVotes());
        if(_issue.getState().equals("Closed")){
            _issue.setClosedAt(LocalDateTime.now().toString());
        }
        else if(_issue.getState().equals("Open")){
            _issue.setUpdatedAt(LocalDateTime.now().toString());
        }
        issueRepository.save(_issue);
    }
}
