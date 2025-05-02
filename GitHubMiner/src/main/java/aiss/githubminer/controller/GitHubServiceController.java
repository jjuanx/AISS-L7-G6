package aiss.githubminer.controller;

import aiss.githubminer.Service.GitHubService;
import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.gitminer.GMProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/github")
public class GitHubServiceController {

    @Autowired
    GitHubService gitHubService;

    @Autowired
    private RestTemplate restTemplate;

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
   @GetMapping("/{owner}/{repo}")
   public GMProject getProject(@PathVariable String owner,
                                      @PathVariable String repo,
                                      @RequestParam(defaultValue = "2") Integer sinceCommits,
                                      @RequestParam(defaultValue = "20") Integer sinceIssues,
                                      @RequestParam(defaultValue = "2") Integer maxPages){
       return gitHubService.createProject(owner, repo, maxPages, sinceCommits, sinceIssues);
   }

    //"http://localhost:8082/github/spring-projects/spring-framework?maxPages=1"
   @PostMapping("/{owner}/{repo}")
    public GMProject sendProjectToGitMiner(@PathVariable String owner,
                                      @PathVariable String repo,
                                      @RequestParam(defaultValue = "2") Integer sinceCommits,
                                      @RequestParam(defaultValue = "20") Integer sinceIssues,
                                      @RequestParam(defaultValue = "2") Integer maxPages){
       GMProject project = gitHubService.createProject(owner, repo, maxPages, sinceCommits, sinceIssues);
       String gitMinerUrl = "http://localhost:8080/gitminer/projects";
       restTemplate.postForObject(gitMinerUrl, project, GMProject.class);
       return project;
   }


}
