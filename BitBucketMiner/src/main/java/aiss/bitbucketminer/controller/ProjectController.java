package aiss.bitbucketminer.controller;

import aiss.bitbucketminer.model.gitminer.Commit;
import aiss.bitbucketminer.model.gitminer.Issue;
import aiss.bitbucketminer.model.gitminer.Project;
import aiss.bitbucketminer.service.CommitService;
import aiss.bitbucketminer.service.IssueService;
import aiss.bitbucketminer.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bitbucket")
public class ProjectController {

    @Autowired
    CommitService commitService;
    @Autowired
    IssueService issueService;

    @GetMapping("/{workspace}/{repoSlug}/commits")
    public List<Commit> previewCommits(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        return commitService.fetchCommitsFromBitbucket(workspace, repoSlug, nCommits, maxPages);
    }

    @PostMapping("/{workspace}/{repoSlug}/commits")
    public void sendCommits(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        commitService.fetchAndSend(workspace, repoSlug, nCommits, maxPages);
    }

    @GetMapping("/{workspace}/{repoSlug}/issues")
    public List<Issue> previewIssues(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(defaultValue = "5") int nIssues,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        return issueService.fetchIssuesFromBitbucket(workspace, repoSlug, nIssues, maxPages);
    }

    @PostMapping("/{workspace}/{repoSlug}/issues")
    public void sendIssues(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(defaultValue = "5") int nIssues,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        issueService.fetchAndSend(workspace, repoSlug, nIssues, maxPages);
    }

    @Autowired
    ProjectService projectService;

    @GetMapping("/{workspace}/{repoSlug}/project")
    public Project previewProject(
            @PathVariable String workspace,
            @PathVariable String repoSlug
    ) {
        return projectService.fetchProjectFromBitbucket(workspace, repoSlug);
    }

    @PostMapping("/{workspace}/{repoSlug}/project")
    public void sendProject(
            @PathVariable String workspace,
            @PathVariable String repoSlug
    ) {
        projectService.fetchAndSend(workspace, repoSlug);
    }

}

