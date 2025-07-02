package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.DataModel.IssueData.IssueDatum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IssuesService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${github.baseuri}")
    private String baseUri;

    @Value("${github.token}")
    private String token;

    //  https://api.github.com/repos/OWNER/REPO/issues
    public List<IssueDatum> getIssues(String owner, String repo, Integer maxPages, Integer sinceIssues){
        List<IssueDatum> issues = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        today = today.minusDays(sinceIssues);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String day = today.format(formato);
        int page = 1;
        String uri = baseUri+"repos/"+owner+ "/"+repo + "/issues?since="+day+"&page="+page;
        HttpHeaders headers = new HttpHeaders();
        if(!Objects.equals(token, "")) {
            headers.set("Authorization", "Bearer "+token);
        }
        try {
            HttpEntity<IssueDatum[]> request = new HttpEntity<>(null, headers);
            ResponseEntity<IssueDatum[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, IssueDatum[].class);
            List<IssueDatum> issues1 = Arrays.asList(response.getBody());
            issues.addAll(issues1);
            while (page < maxPages) {
                page++;
                uri = baseUri + "repos/" + owner + "/" + repo + "/issues?since=" + day + "&page=" + page;
                response = restTemplate.exchange(uri, HttpMethod.GET, request, IssueDatum[].class);
                List<IssueDatum> issues2 = Arrays.asList(response.getBody());
                issues.addAll(issues2);
            }
            return issues;
        } catch (RestClientException e) {
            throw new ExternalApiException("Error encontrando issues: " + e.getMessage());
        }
    }
}
