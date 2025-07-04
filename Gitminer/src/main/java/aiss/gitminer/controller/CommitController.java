package aiss.gitminer.controller;

import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.CommitRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "Commit", description = "Commit management API")
@RestController
@RequestMapping("/gitminer")
public class CommitController {

    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private ProjectRepository projectRepository;

    // GET http://localhost:8080/gitminer/commits
    @Operation(
            summary = "Retrieve all Commits",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of commits",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Commit.class)) }
            )
    })
    @GetMapping("/commits")
    public List<Commit> getAllCommits(
            @Parameter(description = "Page number to be retrieved") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of projects per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "attribute to be filtered") @RequestParam(required = false) String title,
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
        Page<Commit> pageCommits;
        if (title != null)
            pageCommits = commitRepository.findByTitle(title, paging);
        else
            pageCommits = commitRepository.findAll(paging);
        return pageCommits.getContent();
    }

    // GET http://localhost:8080/gitminer/commits/{id}
    @Operation(
            summary = "Retrieve a Commit by Id",
            description = "Get a Commit object by specifying its id",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Commit found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Commit.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commit not found",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping("/commits/{id}")
    public Commit getCommitById(@Parameter(description = "id of commit to be searched", required = true)@PathVariable String id) throws CommitNotFoundException {
        Optional<Commit> commit = commitRepository.findById(id);
        if (!commit.isPresent()) {
            throw new CommitNotFoundException();
        }
        return commit.get();
    }

    // GET http://localhost:8080/gitminer/projects/{projectId}/commits
    @Operation(
            summary = "Retrieve all Commits by Project Id",
            description = "Get all Commits object by specifying its Project id",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Commit found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Commit.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commit not found",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping("/projects/{projectId}/commits")
    public List<Commit> getAllCommitsByProjectId(@Parameter(description = "id of the project whose commits are to be retrieved", required = true)@PathVariable(value="projectId") String projectId) throws ProjectNotFoundException{
        Optional<Project> project = projectRepository.findById(projectId);
        if(!project.isPresent()){
            throw new ProjectNotFoundException();
        }
        List<Commit> commits = new ArrayList<>(project.get().getCommits());
        return commits;
    }

    // POST http://localhost:8080/gitminer/projects/{projectId}/commits
    @Operation(
            summary = "Create a Commit",
            description = "Create a Commit object by specifying its content and his Project id",
            tags = {"create"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Commit successfully created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Commit.class)) }
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
    @PostMapping("/projects/{projectId}/commits")
    public Commit createCommit(
            @Parameter(description = "Commit object to be created", required = true)
            @Valid @RequestBody Commit commit,
            @Parameter(description = "id of the project to associate the commit with", required = true)
            @PathVariable String projectId) throws ProjectNotFoundException {
        Optional < Project> project = projectRepository.findById(projectId);
        if (!project.isPresent()) {
            throw new ProjectNotFoundException();
        }
        Commit newCommit = new Commit(
                commit.getId(),
                commit.getTitle(),
                commit.getMessage(),
                commit.getAuthoredDate(),
                commit.getAuthorEmail(),
                commit.getAuthorName(),
                commit.getWebUrl());
        project.get().getCommits().add(newCommit);
        projectRepository.save(project.get());
        return newCommit;
    }

    // DELETE http://localhost:8080/gitminer/commits/{id}
    @Operation(
            summary = "Delete a Commit by Id",
            description = "Delete a Commit object by specifying its id",
            tags = {"delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Commit successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commit not found",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/commits/{id}")
    public void deleteCommit(@Parameter(description = "id of commit to be deleted", required = true)@PathVariable String id) throws CommitNotFoundException {
        if(commitRepository.existsById(id)) {
            commitRepository.deleteById(id);
        }else{
            throw new CommitNotFoundException();
        }
    }

    //PUT http://localhost:8080/gitminer/commits/{id}
    @Operation(
            summary = "Update a Commit by Id",
            description = "Update a Commit object by specifying its id",
            tags = {"update"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Commit successfully updated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Commit not found",
                    content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/commits/{id}")
    public void updateCommit(
            @Parameter(description = "id of commit to be updated", required = true)
            @PathVariable String id,
            @Parameter(description = "Commit object to be updated", required = true)
            @RequestBody Commit commit
    ) throws CommitNotFoundException {
        Optional <Commit> commitData = commitRepository.findById(id);
        if (!commitData.isPresent()) {
            throw new CommitNotFoundException();
        }
        Commit newCommit = commitData.get();
        newCommit.setTitle(commit.getTitle());
        newCommit.setMessage(commit.getMessage());
        newCommit.setAuthoredDate(commit.getAuthoredDate());
        newCommit.setAuthorEmail(commit.getAuthorEmail());
        newCommit.setAuthorName(commit.getAuthorName());
        newCommit.setWebUrl(commit.getWebUrl());
        commitRepository.save(newCommit);
    }

}
