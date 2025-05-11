package aiss.bitbucketminer.controller;

import aiss.bitbucketminer.model.gitminer.Commit;
import aiss.bitbucketminer.model.gitminer.Issue;
import aiss.bitbucketminer.model.gitminer.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProjectControllerTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8081/bitbucket";
    private final String workspace = "gentlero";
    private final String repoSlug = "bitbucket-api";

    @Test
    @DisplayName("GET /{workspace}/{repoSlug}/commits returns list of commits")
    void getCommits() {
        ResponseEntity<List<Commit>> response = restTemplate.exchange(
                baseUrl + "/" + workspace + "/" + repoSlug + "/commits?nCommits=3&maxPages=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /{workspace}/{repoSlug}/issues returns list of issues")
    void getIssues() {
        ResponseEntity<List<Issue>> response = restTemplate.exchange(
                baseUrl + "/" + workspace + "/" + repoSlug + "/issues?nIssues=3&maxPages=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /{workspace}/{repoSlug}/project returns Project with basic info")
    void getProject() {
        ResponseEntity<Project> response = restTemplate.getForEntity(
                baseUrl + "/" + workspace + "/" + repoSlug,
                Project.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Project project = response.getBody();
        assertNotNull(project);
        assertEquals(repoSlug, project.getName());
    }

    @Test
    @DisplayName("GET /{workspace}/{repoSlug} returns full Project with commits and issues")
    void getFullProject() {
        ResponseEntity<Project> response = restTemplate.getForEntity(
                baseUrl + "/" + workspace + "/" + repoSlug + "?nCommits=3&nIssues=3&maxPages=1",
                Project.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Project project = response.getBody();
        assertNotNull(project);
        assertEquals(repoSlug, project.getName());
        assertNotNull(project.getCommits());
        assertNotNull(project.getIssues());
    }
}