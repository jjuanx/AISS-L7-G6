package aiss.githubminer.controller;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.gitminer.GMProject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommitsControllerTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8082/github";
    private final String owner = "spring-projects";
    private final String repo = "spring-framework";

    @Test
    @DisplayName("GET /{owner}/{repo}/commits returns list of commits")
    void getCommits() {
        ResponseEntity<List<Commit>> response = restTemplate.exchange(
                baseUrl + "/" + owner + "/" + repo + "/commits",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /{owner}/{repo}/issues returns list of issues")
    void getIssues() {
        ResponseEntity<List<Issue>> response = restTemplate.exchange(
                baseUrl + "/" + owner + "/" + repo + "/issues",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /{owner}/{repo}/issues/comments returns list of comments")
    void getComments() {
        ResponseEntity<List<Comment>> response = restTemplate.exchange(
                baseUrl + "/" + owner + "/" + repo + "/issues/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /{owner}/{repo} returns GMProject with data")
    void getProject() {
        ResponseEntity<GMProject> response = restTemplate.getForEntity(
                baseUrl + "/" + owner + "/" + repo,
                GMProject.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GMProject project = response.getBody();
        assertNotNull(project);
        assertEquals(repo, project.getName());
    }
}