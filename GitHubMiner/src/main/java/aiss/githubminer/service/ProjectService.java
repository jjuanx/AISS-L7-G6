package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.exception.GitMinerPostException;
import aiss.githubminer.model.DataModel.ProjectData;
import aiss.githubminer.model.GitMinerModel.ProjectGitMiner;
import aiss.githubminer.tranformer.CommitMapper;
import aiss.githubminer.tranformer.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CommitMapper commitMapper;
    @Autowired
    IssueMapper issueMapper;

    @Value("${github.baseuri}")
    private String baseUri;

    @Value("${github.token}")
    private String token;

    public ProjectData getProject(String workspace, String repo) {
        String uri = baseUri + "repos/" + workspace + "/" + repo;
        HttpHeaders headers = new HttpHeaders();
        if(!Objects.equals(token, "")) {
            headers.set("Authorization", "Bearer "+token);
        }
        HttpEntity<ProjectData> request = new HttpEntity<>(null, headers);
        ResponseEntity<ProjectData> response = restTemplate.exchange(uri, HttpMethod.GET, request, ProjectData.class);
        return response.getBody();
    }

    public ProjectGitMiner createProject(String owner, String repo,Integer maxPages, Integer sinceCommits, Integer sinceIssues) {
        ProjectGitMiner project = new ProjectGitMiner();
        ProjectData data = getProject(owner, repo);
        project.setId(data.getId().toString());
        project.setName(data.getName());
        project.setWebUrl(data.getHtmlUrl());
        project.setCommits(commitMapper.getCommits(owner, repo, sinceCommits, maxPages));
        project.setIssues(issueMapper.getIssues(owner, repo, sinceIssues, maxPages));
        return project;
    }

    public ProjectGitMiner sendToGitMiner(ProjectGitMiner project) {
        String gitMinerUrl = "http://localhost:8080/gitminer/projects";
        try {
            HttpEntity<ProjectGitMiner> request = new HttpEntity<>(project);
            ResponseEntity<ProjectGitMiner> response = restTemplate.exchange(gitMinerUrl, HttpMethod.POST, request, ProjectGitMiner.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new GitMinerPostException("No se pudo enviar el proyecto a GitMiner: " + e.getMessage());
        }
    }
}
