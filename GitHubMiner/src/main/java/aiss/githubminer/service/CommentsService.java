package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.DataModel.CommentData.CommentDatum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CommentsService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${github.token}")
    private String token;

    //https://api.github.com/repos/OWNER/REPO/issues/comments
    public List<CommentDatum> getComments(String url) {
        String uri = url;
        HttpHeaders headers = new HttpHeaders();
        if(!Objects.equals(token, "")){
            headers.add("Authorization", "Bearer " + token);
        }
        HttpEntity<CommentDatum[]> request = new HttpEntity<>(null, headers);
        ResponseEntity<CommentDatum[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, CommentDatum[].class);
        List<CommentDatum> comments = Arrays.asList(response.getBody());
        return comments;
    }
}
