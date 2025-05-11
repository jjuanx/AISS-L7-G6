package aiss.bitbucketminer.service;

import aiss.bitbucketminer.etl.Transformer;
import aiss.bitbucketminer.model.bitbucket.BitBucketProject;
import aiss.bitbucketminer.model.gitminer.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProjectService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitminer.url}")
    private String gitminerUrl;

    public Project fetchProjectFromBitbucket(String workspace, String repoSlug) {
        String url = String.format(
                "https://api.bitbucket.org/2.0/repositories/%s/%s",
                workspace, repoSlug
        );

        BitBucketProject bitbucketProject = restTemplate.getForObject(url, BitBucketProject.class);

        return Transformer.toGitMinerProject(bitbucketProject);
    }

    public void sendProjectToGitMiner(Project project) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Project> request = new HttpEntity<>(project, headers);
        restTemplate.postForEntity(gitminerUrl + "/projects", request, Void.class);
    }

    public void fetchAndSend(String workspace, String repoSlug) {
        Project project = fetchProjectFromBitbucket(workspace, repoSlug);
        sendProjectToGitMiner(project);
    }
}

