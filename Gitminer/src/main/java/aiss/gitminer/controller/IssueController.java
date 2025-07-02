package aiss.gitminer.controller;

import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of issues",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class)) }
            )
    })
    @GetMapping("/issues")
    public List<Issue> getAllIssues(
            @Parameter(description = "Page number to be retrieved") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of projects per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "attribute to be filtered") @RequestParam(required = false) String authorId,
            @Parameter(description = "attribute to be filtered") @RequestParam(required = false) String state,
            @Parameter(description = "order of the request retrieved") @RequestParam(required = false) String order
    ) {
        Pageable paging;

        if(order != null) {
            if (order.startsWith("-")) {
                paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
            } else {
                paging = PageRequest.of(page, size, Sort.by(order).ascending());
            }
        }else {
            paging = PageRequest.of(page, size);
        }
        Page<Issue> pageIssues;
        if (authorId != null && state != null) {
            pageIssues = issueRepository.findByStateAndAuthorId(state, authorId, paging);
        } else if (authorId != null && state == null) {
            pageIssues = issueRepository.findByAuthorId(authorId, paging);
        } else if (authorId == null && state != null) {
            pageIssues = issueRepository.findByState(state, paging);
        } else {
            pageIssues = issueRepository.findAll(paging);
        }
        return pageIssues.getContent();
    }

    // GET http://localhost:8080/gitminer/issues/{id}
    @Operation(
            summary = "Retrieve a Issue by Id",
            description = "Get a Issue object by specifying its id",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Issue found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Issue not found",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping("/issues/{id}")
    public Issue getIssueById(@Parameter(description = "id of issue to be searched", required = true)@PathVariable String id) throws IssueNotFoundException {
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Issues found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping("/projects/{projectId}/issues")
    public List<Issue> getAllIssuesByProjectId(@Parameter(description = "id of the project whose issues are to be retrieved", required = true)@PathVariable(value="projectId") String projectId) throws ProjectNotFoundException {
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Issue successfully created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/projects/{projectId}/issues")
    public Issue createIssue(
            @Parameter(description = "Issue object to be created", required = true)
            @RequestBody @Valid Issue issue,
            @Parameter(description = "id of the project to associate the issue with", required = true)
            @PathVariable String projectId
    ) throws ProjectNotFoundException {
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
                issue.getVotes(),
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Issue successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Issue not found",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/issues/{id}")
    public void deleteIssue(@Parameter(description = "id of issue to be deleted", required = true)@PathVariable String id) throws IssueNotFoundException {
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Issue successfully updated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Issue not found",
                    content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/issues/{id}")
    public void updateIssue(
            @Parameter(description = "Issue object to be updated", required = true)
            @RequestBody @Valid Issue issue,
            @Parameter(description = "id of issue to be updated", required = true)
            @PathVariable String id
    ) throws IssueNotFoundException {
        Optional <Issue> issueData = issueRepository.findById(id);
        if(!issueData.isPresent()){
            throw new IssueNotFoundException();
        }
        Issue _issue = issueData.get();
        _issue.setState(issue.getState());
        _issue.setLabels(issue.getLabels());
        _issue.setDescription(issue.getDescription());
        _issue.setVotes(issue.getVotes());
        _issue.setTitle(issue.getTitle());
        _issue.setVotes(issue.getVotes());
        _issue.setAuthor(issue.getAuthor());
        _issue.setComments(issue.getComments());
        _issue.setAssignee(issue.getAssignee());
        if(_issue.getState().equals("closed")){
            _issue.setClosedAt(LocalDateTime.now().toString());
        }
        else if(_issue.getState().equals("open")){
            _issue.setUpdatedAt(LocalDateTime.now().toString());
        }
        issueRepository.save(_issue);
    }
}
