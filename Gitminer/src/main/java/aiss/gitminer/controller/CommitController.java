package aiss.gitminer.controller;

import aiss.gitminer.model.Commit;
import aiss.gitminer.repositories.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {

    @Autowired
    private CommitRepository commitRepository;

    @GetMapping
    public List<Commit> getAllCommits() {
        return commitRepository.findAll();
    }

    @GetMapping("/{id}")
    public Commit getCommitById(@PathVariable String id) {
        Optional<Commit> commit = commitRepository.findById(id);
        return commit.orElse(null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Commit createCommit(@Valid @RequestBody Commit commit) {
        return commitRepository.save(commit);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCommit(@PathVariable String id) {
        if(commitRepository.existsById(id)) {
            commitRepository.deleteById(id);
        }
    }








}
