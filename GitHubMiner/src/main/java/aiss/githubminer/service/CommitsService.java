package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.DataModel.CommitData.CommitDatum;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CommitsService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${github.token}")
    private String token;

    @Value("${github.baseuri}")
    private String baseUri;

    //https://api.github.com/repos/OWNER/REPO/commits

    public List<CommitDatum> getCommits(String owner, String repo, Integer maxPages, Integer sinceCommits){
        List<CommitDatum> commits = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        today = today.minusDays(sinceCommits);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String day = today.format(formato);
        int page = 1;
        String uri = baseUri+"repos/"+owner+ "/"+repo + "/commits?since="+day+"&page="+page;
        HttpHeaders headers = new HttpHeaders();
        if(!Objects.equals(token, "")) {
            headers.set("Authorization", "Bearer "+token);
        }
        try {
            HttpEntity<CommitDatum[]> request = new HttpEntity<>(null, headers);
            ResponseEntity<CommitDatum[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, CommitDatum[].class);
            List<CommitDatum> commits1 = Arrays.asList(response.getBody());
            commits.addAll(commits1);
            while (page < maxPages) {
                page++;
                uri = baseUri + "repos/" + owner + "/" + repo + "/issues?since=" + day + "&page=" + page;
                response = restTemplate.exchange(uri, HttpMethod.GET, request, CommitDatum[].class);
                List<CommitDatum> commits2 = Arrays.asList(response.getBody());
                commits.addAll(commits2);
            }
            return commits;
        } catch (RestClientException e) {
            throw new ExternalApiException("Error encontrando commits: " + e.getMessage());
        }
    }
}
