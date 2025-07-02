package aiss.bitbucketminer.service;

import aiss.bitbucketminer.etl.Transformer;
import aiss.bitbucketminer.exception.BitbucketApiException;
import aiss.bitbucketminer.exception.InvalidParameterException;
import aiss.bitbucketminer.exception.RepositoryNotFoundException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

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

        // Validación de parámetros
        if (workspace == null || workspace.isBlank()) {
            throw new InvalidParameterException("El workspace no puede estar vacío");
        }
        if (repoSlug == null || repoSlug.isBlank()) {
            throw new InvalidParameterException("El repoSlug no puede estar vacío");
        }
        if (nCommits < 0 || nIssues < 0 || maxPages < 1) {
            throw new InvalidParameterException("Parámetros inválidos: nCommits, nIssues o maxPages");
        }

        String url = String.format(
                "https://api.bitbucket.org/2.0/repositories/%s/%s",
                workspace, repoSlug
        );

        BitBucketProject bitbucketProject;

        // Manejo de errores en la llamada a Bitbucket API
        try {
            bitbucketProject = restTemplate.getForObject(url, BitBucketProject.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RepositoryNotFoundException("Repositorio no encontrado: " + workspace + "/" + repoSlug);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new BitbucketApiException("Error en la petición a Bitbucket API: " + e.getMessage());
        } catch (Exception e) {
            throw new BitbucketApiException("Error inesperado al obtener el proyecto: " + e.getMessage());
        }

        // Transformación y carga
        Project gitMinerProject = Transformer.toGitMinerProject(bitbucketProject);
        List<Commit> commits = commitService.fetchCommitsFromBitbucket(workspace, repoSlug, nCommits, maxPages);
        List<Issue> issues = issueService.fetchIssuesFromBitbucket(workspace, repoSlug, nIssues, maxPages);
        gitMinerProject.setCommits(commits);
        gitMinerProject.setIssues(issues);
        return gitMinerProject;
    }

    public void sendProjectToGitMiner(Project project) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Project> request = new HttpEntity<>(project, headers);
        try {
            restTemplate.postForEntity(gitminerUrl + "/projects", request, Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new BitbucketApiException("Error al enviar el proyecto a GitMiner: " + e.getMessage());
        } catch (Exception e) {
            throw new BitbucketApiException("Error inesperado al enviar el proyecto a GitMiner: " + e.getMessage());
        }
    }

    public void fetchAndSend(String workspace, String repoSlug, int nCommits, int maxPages, int nIssues) {
        Project project = fetchProjectFromBitbucket(workspace, repoSlug, nCommits, maxPages, nIssues);
        sendProjectToGitMiner(project);
    }
}
