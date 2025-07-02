package aiss.bitbucketminer.service;

import aiss.bitbucketminer.etl.Transformer;
import aiss.bitbucketminer.exception.BitbucketApiException;
import aiss.bitbucketminer.exception.InvalidParameterException;
import aiss.bitbucketminer.exception.RepositoryNotFoundException;
import aiss.bitbucketminer.model.bitbucket.BitBucketIssue;
import aiss.bitbucketminer.model.gitminer.Comment;
import aiss.bitbucketminer.model.gitminer.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IssueService {

    @Autowired
    CommentService commentService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitminer.url}")
    private String gitminerUrl;

    public List<Issue> fetchIssuesFromBitbucket(String workspace, String repoSlug, int nIssues, int maxPages) {
        if (workspace == null || workspace.isBlank()) {
            throw new InvalidParameterException("El workspace no puede estar vacío");
        }
        if (repoSlug == null || repoSlug.isBlank()) {
            throw new InvalidParameterException("El repoSlug no puede estar vacío");
        }
        if (nIssues <= 0) {
            throw new InvalidParameterException("nIssues debe ser mayor que 0");
        }
        if (maxPages <= 0) {
            throw new InvalidParameterException("maxPages debe ser mayor que 0");
        }

        List<Issue> result = new ArrayList<>();

        String baseUrl = String.format(
                "https://api.bitbucket.org/2.0/repositories/%s/%s/issues?pagelen=%d",
                workspace, repoSlug, nIssues
        );

        String nextPage = baseUrl;
        int page = 0;

        while (nextPage != null && page < maxPages) {
            try {
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        nextPage,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {}
                );

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rawIssues = (List<Map<String, Object>>) response.getBody().get("values");

                if (rawIssues == null) {
                    break; // no issues found on this page
                }

                for (Map<String, Object> raw : rawIssues) {
                    Integer issueId = (Integer) raw.get("id");

                    try {
                        BitBucketIssue bitbucketIssue = restTemplate.getForObject(
                                "https://api.bitbucket.org/2.0/repositories/" + workspace + "/" + repoSlug + "/issues/" + issueId,
                                BitBucketIssue.class
                        );

                        if (bitbucketIssue == null) {
                            throw new BitbucketApiException("Issue con ID " + issueId + " no encontrado.");
                        }

                        Issue issue = Transformer.toGitMinerIssue(bitbucketIssue);

                        List<Comment> comments = commentService.fetchCommentsForIssue(workspace, repoSlug, issueId);
                        issue.setComments(comments);

                        result.add(issue);

                    } catch (HttpClientErrorException.NotFound e) {
                        throw new BitbucketApiException("Issue con ID " + issueId + " no encontrado.");
                    } catch (HttpClientErrorException e) {
                        throw new BitbucketApiException("Error en la petición de issue " + issueId + ": " + e.getMessage());
                    }
                }

                nextPage = (String) response.getBody().get("next");
                page++;

            } catch (HttpClientErrorException.NotFound e) {
                // Si la primera llamada da 404, el repo no existe
                throw new RepositoryNotFoundException("Repositorio " + workspace + "/" + repoSlug + " no encontrado en Bitbucket.");
            } catch (HttpClientErrorException e) {
                throw new BitbucketApiException("Error en la petición a Bitbucket API: " + e.getMessage());
            } catch (Exception e) {
                throw new BitbucketApiException("Error inesperado: " + e.getMessage());
            }
        }

        return result;
    }

}
