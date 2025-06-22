package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.exception.GitMinerPostException;
import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.gitminer.GMCommit;
import aiss.githubminer.model.gitminer.GMIssue;
import aiss.githubminer.model.gitminer.GMProject;
import aiss.githubminer.tranformer.CommitMapper;
import aiss.githubminer.tranformer.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CommitsService commitsService;

    @Autowired
    CommentsService commentsService;

    @Autowired
    IssuesService issuesService;

    private final String baseUri = "https://api.github.com/";

    //https://api.github.com/repos/OWNER/REPO
    public GMProject getProject(String owner, String repo) {
        String uri = baseUri + "repos/" + owner + "/" + repo;
        try {
            return restTemplate.getForObject(uri, GMProject.class);
        } catch (RestClientException e) {
            throw new ExternalApiException("Error encontrando los datos del Proyecto Github: " + e.getMessage());
        }
    }

    public GMProject createProject(String owner, String repo,Integer maxPages, Integer sinceCommits, Integer sinceIssues) {
        // Paso 1: Recolectamos los datos
        List<Commit> commits = commitsService.getCommits(owner, repo, maxPages, sinceCommits);
        List<Issue> issues = issuesService.getIssues(owner, repo, maxPages, sinceIssues);
        List<Comment> comments = commentsService.getComments(owner, repo, maxPages);
        GMProject metaData = getProject(owner,repo);

        // Paso 2: Transformar los datos
        List<GMCommit> gmCommits = CommitMapper.mapCommits(commits, "https://github.com/" + owner + "/" + repo);
        List<GMIssue> gmIssues = IssueMapper.mapIssues(issues, comments);

        // Paso 3: Crear el GMProject
        GMProject gmProject = new GMProject();
        gmProject.setId(String.valueOf(metaData.getId()));
        gmProject.setName(metaData.getName());
        gmProject.setWebUrl("https://github.com/" + owner + "/" + repo);
        gmProject.setCommits(gmCommits);
        gmProject.setIssues(gmIssues);

        return gmProject;
    }

    public void sendToGitMiner(GMProject project) {
        String gitMinerUrl = "http://localhost:8080/gitminer/projects";
        try {
            restTemplate.postForObject(gitMinerUrl, project, GMProject.class);
        } catch (RestClientException e) {
            throw new GitMinerPostException("No se pudo enviar el proyecto a GitMiner: " + e.getMessage());
        }
    }
}
