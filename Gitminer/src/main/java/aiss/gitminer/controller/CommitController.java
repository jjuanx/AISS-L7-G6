package aiss.gitminer.controller;

import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.CommitRepository;
import aiss.gitminer.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer")
public class CommitController {

    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private ProjectRepository projectRepository;

    // GET http://localhost:8080/gitminer/commits
    @GetMapping("/commits")
    public List<Commit> getAllCommits() {
        return commitRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/commits/{id}
    @GetMapping("/commits/{id}")
    public Commit getCommitById(@PathVariable String id) throws CommitNotFoundException {
        Optional<Commit> commit = commitRepository.findById(id);
        if (!commit.isPresent()) {
            throw new CommitNotFoundException();
        }
        return commit.get();
    }

    // GET http://localhost:8080/gitminer/projects/{projectId}/commits
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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/projects/{projectId}/commits")
    public Commit createCommit(@Valid @RequestBody Commit commit, @PathVariable String projectId) throws ProjectNotFoundException {
        Optional < Project> project = projectRepository.findById(projectId);
        if (!project.isPresent()) {
            throw new ProjectNotFoundException();
        }
        Commit newCommit = new Commit(commit.getTitle(),commit.getMessage(), commit.getAuthoredDate(),commit.getAuthorEmail(),commit.getAuthorName(), commit.getWebUrl());
        project.get().getCommits().add(newCommit);
        projectRepository.save(project.get());
        return newCommit;
    }

    // DELETE http://localhost:8080/gitminer/commits/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/commits/{id}")
    public void deleteCommit(@PathVariable String id) throws CommitNotFoundException {
        if(commitRepository.existsById(id)) {
            commitRepository.deleteById(id);
        }else{
            throw new CommitNotFoundException();
        }
    }

}
