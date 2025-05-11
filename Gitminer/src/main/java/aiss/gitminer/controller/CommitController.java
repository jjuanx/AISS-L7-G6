package aiss.gitminer.controller;

import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.CommitRepository;
import aiss.gitminer.repositories.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/commits")
    public List<Commit> getAllCommits() {
        return commitRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/commits/{id}
    @Operation(
            summary = "Retrieve a Commit by Id",
            description = "Get a Commit object by specifying its id",
            tags = {"get"}
    )
    @GetMapping("/commits/{id}")
    public Commit getCommitById(@PathVariable String id) throws CommitNotFoundException {
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
    @GetMapping("/projects/{projectId}/commits")
    public List<Commit> getAllCommitsByProjectId(@PathVariable(value="projectId") String projectId) throws ProjectNotFoundException{
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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/projects/{projectId}/commits")
    public Commit createCommit(@Valid @RequestBody Commit commit, @PathVariable String projectId) throws ProjectNotFoundException {
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/commits/{id}")
    public void deleteCommit(@PathVariable String id) throws CommitNotFoundException {
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/commits/{id}")
    public void updateCommit(@PathVariable String id, @RequestBody Commit commit) throws CommitNotFoundException {
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
