package aiss.githubminer.controller;

import aiss.githubminer.Service.GitHubService;
import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/github")
public class GitHubServiceController {

    @Autowired
    GitHubService gitHubService;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/{owner}/{repo}/commits")
    public List<Commit> getCommits(@PathVariable String owner, @PathVariable String repo,
                                   @RequestParam(required = false) Integer maxPages,
                                   @RequestParam(required = false) Integer sinceCommits) {
        return gitHubService.getCommits(owner, repo, maxPages, sinceCommits);
    }

    @GetMapping("/{owner}/{repo}/issues")
    public List<Issue> getIssues(@PathVariable String owner, @PathVariable String repo,
                                 @RequestParam(required = false) Integer maxPages,
                                 @RequestParam(required = false) Integer sinceIssues) {
        return gitHubService.getIssues(owner, repo, maxPages, sinceIssues);
    }

    @GetMapping("/{owner}/{repo}/issues/comments")
    public List<Comment> getComments(@PathVariable String owner,
                                     @PathVariable String repo,
                                     @RequestParam(required = false) Integer maxPages
    ) {
        return gitHubService.getComments(owner, repo, maxPages);
    }

    //"http://localhost:8082/github/spring-projects/spring-framework"
   // @GetMapping("/{owner}/{repo}")
   // public

    //"http://localhost:8082/github/spring-projects/spring-framework?maxPages=1"
    //@PostMapping("/{owner}/{repo}/")


}
