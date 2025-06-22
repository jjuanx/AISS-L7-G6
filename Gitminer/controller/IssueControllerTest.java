package aiss.gitminer.controller;

import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IssueControllerTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/gitminer/issues";
    private final String projectBaseUrl = "http://localhost:8080/gitminer/projects";

    private Project savedProject;

    @BeforeEach
    void setUp() {
        // Creamos un proyecto base para asociar los issues
        Project project = new Project();
        project.setId("project1");
        project.setName("Test Project");
        project.setWebUrl("http://web.com/project");
        project.setCommits(Collections.emptyList());
        project.setIssues(Collections.emptyList());

        ResponseEntity<Project> response = restTemplate.postForEntity(projectBaseUrl, project, Project.class);
        savedProject = response.getBody();
        assertNotNull(savedProject);
    }

    @Test
    @DisplayName("GET /issues devuelve la lista de issues")
    void getAllIssues() {
        ResponseEntity<List<Issue>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /projects/{id}/issues crea un issue")
    void createIssue() {
        Issue issue = new Issue();
        issue.setId("issue1");
        issue.setTitle("New issue");
        issue.setState("open");

        ResponseEntity<Issue> response = restTemplate.postForEntity(
                projectBaseUrl + "/" + savedProject.getId() + "/issues",
                issue,
                Issue.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New issue", response.getBody().getTitle());
    }

    @Test
    @DisplayName("GET /issues/{id} devuelve un issue si existe")
    void getIssueById() {
        // Crear issue
        Issue issue = new Issue();
        issue.setId("issue2");
        issue.setTitle("Issue to retrieve");
        issue.setState("open");

        restTemplate.postForEntity(projectBaseUrl + "/" + savedProject.getId() + "/issues", issue, Issue.class);

        // Recuperar
        ResponseEntity<Issue> response = restTemplate.getForEntity(baseUrl + "/issue2", Issue.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Issue to retrieve", response.getBody().getTitle());
    }

    @Test
    @DisplayName("DELETE /issues/{id} elimina un issue si existe")
    void deleteIssue() {
        Issue issue = new Issue();
        issue.setId("issue3");
        issue.setTitle("To delete");
        issue.setState("open");

        restTemplate.postForEntity(projectBaseUrl + "/" + savedProject.getId() + "/issues", issue, Issue.class);

        HttpEntity<Void> requestEntity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/issue3",
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /issues/{id} actualiza un issue si existe")
    void updateIssue() {
        Issue issue = new Issue();
        issue.setId("issue4");
        issue.setTitle("Old title");
        issue.setState("open");

        restTemplate.postForEntity(projectBaseUrl + "/" + savedProject.getId() + "/issues", issue, Issue.class);

        // Cambiamos los valores
        issue.setTitle("Updated title");
        issue.setState("closed");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Issue> request = new HttpEntity<>(issue, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/issue4",
                HttpMethod.PUT,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<Issue> updated = restTemplate.getForEntity(baseUrl + "/issue4", Issue.class);
        assertEquals("Updated title", updated.getBody().getTitle());
        assertEquals("closed", updated.getBody().getState());
    }
}
