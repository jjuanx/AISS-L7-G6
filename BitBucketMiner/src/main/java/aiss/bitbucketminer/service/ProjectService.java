package aiss.bitbucketminer.service;

import aiss.bitbucketminer.etl.Transformer;
import aiss.bitbucketminer.model.bitbucket.BitBucketProject;
import aiss.bitbucketminer.model.gitminer.Commit;
import aiss.bitbucketminer.model.gitminer.Issue;
import aiss.bitbucketminer.model.gitminer.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitminer.url}")
    private String gitminerUrl;
    @Autowired
    private CommitService commitService;
    @Autowired
    private IssueService issueService;

    public Project fetchProjectFromBitbucket(String workspace, String repoSlug, int nCommits, int maxPages, int nIssues) {
        String url = String.format(
                "https://api.bitbucket.org/2.0/repositories/%s/%s",
                workspace, repoSlug
        );

        BitBucketProject bitbucketProject = restTemplate.getForObject(url, BitBucketProject.class);

        Project gitMinerProject =  Transformer.toGitMinerProject(bitbucketProject);

        List <Commit> commits = commitService.fetchCommitsFromBitbucket(workspace,repoSlug,nCommits,maxPages);
        List <Issue> issues = issueService.fetchIssuesFromBitbucket(workspace,repoSlug,nIssues, maxPages);

        gitMinerProject.setCommits(commits);
        gitMinerProject.setIssues(issues);
        return gitMinerProject;
    }

    public void sendProjectToGitMiner(Project project) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Project> request = new HttpEntity<>(project, headers);
        restTemplate.postForEntity(gitminerUrl + "/projects", request, Void.class);
    }

    public void fetchAndSend(String workspace, String repoSlug, int nCommits, int maxPages, int nIssues) {
        Project project = fetchProjectFromBitbucket(workspace, repoSlug, nCommits, maxPages, nIssues);
        sendProjectToGitMiner(project);
    }
}

