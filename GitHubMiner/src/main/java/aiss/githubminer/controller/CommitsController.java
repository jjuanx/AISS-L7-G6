package aiss.githubminer.controller;

import aiss.githubminer.service.CommitsService;
import aiss.githubminer.model.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/github")
public class CommitsController {

    @Autowired
    CommitsService commitsService;

    @GetMapping("/{owner}/{repo}/commits")
    public List<Commit> getCommits(@PathVariable String owner, @PathVariable String repo,
                                   @RequestParam(required = false, defaultValue = "2") Integer maxPages,
                                   @RequestParam(required = false, defaultValue = "2") Integer sinceCommits) {
        return commitsService.getCommits(owner, repo, maxPages, sinceCommits);
    }
}
