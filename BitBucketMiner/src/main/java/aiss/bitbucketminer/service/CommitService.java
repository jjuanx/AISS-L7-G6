package aiss.bitbucketminer.service;

import aiss.bitbucketminer.etl.Transformer;
import aiss.bitbucketminer.model.bitbucket.BitbucketCommit;
import aiss.bitbucketminer.model.gitminer.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommitService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitminer.url}")
    private String gitminerUrl;

    public List<Commit> fetchCommitsFromBitbucket(String workspace, String repoSlug, int nCommits, int maxPages) {
        List<Commit> result = new ArrayList<>();

        String baseUrl = String.format(
                "https://api.bitbucket.org/2.0/repositories/%s/%s/commits?pagelen=%d",
                workspace, repoSlug, nCommits
        );

        String nextPage = baseUrl;
        int page = 0;

        while (nextPage != null && page < maxPages) {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    nextPage,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> rawCommits = (List<Map<String, Object>>) response.getBody().get("values");

            for (Map<String, Object> raw : rawCommits) {
                BitbucketCommit rawCommit = restTemplate.getForObject(
                        "https://api.bitbucket.org/2.0/repositories/" + workspace + "/" + repoSlug + "/commit/" + raw.get("hash"),
                        BitbucketCommit.class
                );
                Commit commit = Transformer.toGitMinerCommit(rawCommit);
                result.add(commit);
            }

            nextPage = (String) response.getBody().get("next");
            page++;
        }

        return result;
    }


}

