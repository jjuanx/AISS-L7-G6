package aiss.githubminer.controller;

import aiss.githubminer.service.ProjectService;
import aiss.githubminer.model.gitminer.GMProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/github")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    //"http://localhost:8082/github/spring-projects/spring-framework"
   @GetMapping("/{owner}/{repo}")
   public GMProject getProject(@PathVariable String owner,
                                      @PathVariable String repo,
                                      @RequestParam(defaultValue = "2") Integer sinceCommits,
                                      @RequestParam(defaultValue = "20") Integer sinceIssues,
                                      @RequestParam(defaultValue = "2") Integer maxPages) {
       return projectService.createProject(owner, repo, maxPages, sinceCommits, sinceIssues);
   }

   @PostMapping("/{owner}/{repo}")
    public GMProject sendProjectToGitMiner(@PathVariable String owner,
                                      @PathVariable String repo,
                                      @RequestParam(defaultValue = "2") Integer sinceCommits,
                                      @RequestParam(defaultValue = "20") Integer sinceIssues,
                                      @RequestParam(defaultValue = "2") Integer maxPages){
       GMProject project = projectService.createProject(owner, repo, maxPages, sinceCommits, sinceIssues);
       projectService.sendToGitMiner(project);
       return project;
   }
}
