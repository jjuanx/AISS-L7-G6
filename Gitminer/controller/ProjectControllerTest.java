package aiss.gitminer.controller;

import aiss.gitminer.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
        import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProjectControllerTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/gitminer/projects";

    @Test
    @DisplayName("GET /projects returns all projects")
    void getAllProjects() {
        ResponseEntity<List<Project>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /projects/{id} returns specific project if exists")
    void getProjectById() {
        Project newProject = new Project("testProjectId", "Test Project", "http://test.url", List.of(), List.of());
        restTemplate.postForEntity(baseUrl, newProject, Project.class);

        ResponseEntity<Project> response = restTemplate.getForEntity(baseUrl + "/testProjectId", Project.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Project", response.getBody().getName());
    }

    @Test
    @DisplayName("POST /projects creates a project")
    void createProject() {
        String id = UUID.randomUUID().toString();
        Project newProject = new Project(id, "Created Project", "http://created.url", List.of(), List.of());

        ResponseEntity<Project> response = restTemplate.postForEntity(baseUrl, newProject, Project.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Created Project", response.getBody().getName());
    }

    @Test
    @DisplayName("DELETE /projects/{id} deletes existing project")
    void deleteProject() {
        String id = "deleteProjectId";
        Project toDelete = new Project(id, "To Delete", "http://delete.url", List.of(), List.of());
        restTemplate.postForEntity(baseUrl, toDelete, Project.class);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /projects/{id} updates existing project")
    void updateProject() {
        String id = "updateProjectId";
        Project original = new Project(id, "Original Name", "http://original.url", List.of(), List.of());
        restTemplate.postForEntity(baseUrl, original, Project.class);

        Project updated = new Project(id, "Updated Name", "http://updated.url", List.of(), List.of());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Project> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + id,
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Project updatedResponse = restTemplate.getForObject(baseUrl + "/" + id, Project.class);
        assertEquals("Updated Name", updatedResponse.getName());
        assertEquals("http://updated.url", updatedResponse.getWebUrl());
    }
}
