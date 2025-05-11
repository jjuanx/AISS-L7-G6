package aiss.gitminer.controller;

import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "Issue", description = "Issue management API")
@RestController
@RequestMapping("/gitminer")
public class IssueController {
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ProjectRepository projectRepository;

    // GET http://localhost:8080/gitminer/issues
    @Operation(
            summary = "Retrieve all Issues",
            tags = {"get"}
    )
    @GetMapping("/issues")
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/issues/{id}
    @Operation(
            summary = "Retrieve a Issue by Id",
            description = "Get a Issue object by specifying its id",
            tags = {"get"}
    )
    @GetMapping("/issues/{id}")
    public Issue getIssueById(@PathVariable String id) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(id);
        if(!issue.isPresent()){
            throw new IssueNotFoundException();
        }
        return issue.get();
    }

    // GET http://localhost:8080/gitminer/projects/{projectId}/issues
    @Operation(
            summary = "Retrieve all Issue by Project id",
            description = "Get all Issue object by specifying its Project id",
            tags = {"get"}
    )
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
    @Operation(
            summary = "Create a Issue",
            description = "Create a Issue object by specifying its content and his Project id",
            tags = {"create"}
    )
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
                issue.getId(),
                issue.getAuthor(),
                issue.getComments()
        );
        project.get().getIssues().add(newIssue);
        projectRepository.save(project.get());
        return newIssue;
    }

    // DELETE http://localhost:8080/gitminer/issues/{id}
    @Operation(
            summary = "Delete a Issue by Id",
            description = "Delete a Issue object by specifying its id",
            tags = {"delete"}
    )
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
    @Operation(
            summary = "Update a Issue by Id",
            description = "Update a Issue object by specifying its id",
            tags = {"update"}
    )
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
        if(_issue.getState().equals("closed")){
            _issue.setClosedAt(LocalDateTime.now().toString());
        }
        else if(_issue.getState().equals("open")){
            _issue.setUpdatedAt(LocalDateTime.now().toString());
        }
        issueRepository.save(_issue);
    }
}
