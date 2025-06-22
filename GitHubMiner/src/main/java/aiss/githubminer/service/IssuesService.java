package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class IssuesService {

    @Autowired
    RestTemplate restTemplate;

    private final String baseUri = "https://api.github.com/";

    //  https://api.github.com/repos/OWNER/REPO/issues
    public List<Issue> getIssues(String owner, String repo) {
        return getIssues(owner, repo, 2, 20);
    }

    public List<Issue> getIssues(String owner, String repo, Integer maxPages, Integer sinceIssues){
        int perPage = (maxPages != null) ? maxPages : 2;
        int sinceDays = (sinceIssues != null) ? sinceIssues : 20;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUri + "repos/" + owner + "/" + repo + "/issues")
                .queryParam("per_page", perPage);

        long millis = System.currentTimeMillis() - ((long) sinceDays * 24 * 60 * 60 * 1000);
        Date sinceDate = new Date(millis);
        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        iso.setTimeZone(TimeZone.getTimeZone("UTC"));
        builder.queryParam("since", iso.format(sinceDate));

        String uri = builder.toUriString();
        try {
            Issue[] issues = restTemplate.getForObject(uri, Issue[].class);
            if(issues == null) { return List.of();}
            List<Issue> res = Arrays.asList(issues).stream().toList();
            return res;
        } catch (RestClientException e) {
            throw new ExternalApiException("Error encontrando issues: " + e.getMessage());
        }
    }
}
