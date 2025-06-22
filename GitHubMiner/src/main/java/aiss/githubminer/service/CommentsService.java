package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class CommentsService {

    @Autowired
    RestTemplate restTemplate;


    private final String baseUri = "https://api.github.com/";

    //https://api.github.com/repos/OWNER/REPO/issues/comments
    public List<Comment> getComments(String owner, String repo) {
        return getComments(owner, repo, 2);
    }

    public List<Comment> getComments(String owner, String repo, Integer maxPages) {
        int perPage = (maxPages != null) ? maxPages : 2;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUri + "repos/" + owner + "/" + repo + "/issues/comments")
                .queryParam("per_page", perPage);

        String uri = builder.toUriString();
        try {
            Comment[] comments = restTemplate.getForObject(uri, Comment[].class);
            if(comments==null) { return List.of(); }
            List<Comment> res = Arrays.asList(comments).stream().toList();
            return res;
        } catch (RestClientException e) {
            throw new ExternalApiException("Error encontrando comentarios: " + e.getMessage());
        }
    }
}
