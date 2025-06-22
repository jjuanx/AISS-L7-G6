package aiss.githubminer.controller;

import aiss.githubminer.service.IssuesService;
import aiss.githubminer.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/github")
public class IssuesController {

    @Autowired
    IssuesService issuesService;

    @GetMapping("/{owner}/{repo}/issues")
    public List<Issue> getIssues(@PathVariable String owner, @PathVariable String repo,
                                 @RequestParam(required = false, defaultValue = "2") Integer maxPages,
                                 @RequestParam(required = false, defaultValue = "20") Integer sinceIssues) {
        return issuesService.getIssues(owner, repo, maxPages, sinceIssues);
    }
}
