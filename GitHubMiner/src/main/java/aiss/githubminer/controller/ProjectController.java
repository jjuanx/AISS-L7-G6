package aiss.githubminer.controller;

import aiss.githubminer.model.GitMinerModel.ProjectGitMiner;
import aiss.githubminer.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/github")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    //"http://localhost:8082/github/spring-projects/spring-framework"
   @GetMapping("/{owner}/{repo}")
   public ProjectGitMiner getProject(@PathVariable String owner,
                                     @PathVariable String repo,
                                     @RequestParam(defaultValue = "2") Integer sinceCommits,
                                     @RequestParam(defaultValue = "20") Integer sinceIssues,
                                     @RequestParam(defaultValue = "2") Integer maxPages) {
       return projectService.createProject(owner, repo, maxPages, sinceCommits, sinceIssues);
   }

   @PostMapping("/{owner}/{repo}")
    public ProjectGitMiner sendProjectToGitMiner(@PathVariable String owner,
                                      @PathVariable String repo,
                                      @RequestParam(defaultValue = "2") Integer sinceCommits,
                                      @RequestParam(defaultValue = "20") Integer sinceIssues,
                                      @RequestParam(defaultValue = "2") Integer maxPages){
       ProjectGitMiner project = projectService.createProject(owner, repo, maxPages, sinceCommits, sinceIssues);
       ProjectGitMiner enviado = projectService.sendToGitMiner(project);
       return enviado;
   }
}
