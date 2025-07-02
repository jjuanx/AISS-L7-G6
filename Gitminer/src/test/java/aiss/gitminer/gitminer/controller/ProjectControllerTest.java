package aiss.gitminer.gitminer.controller;

import aiss.gitminer.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/gitminer/projects";
    }

    private Project createProject(String id, String name) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setWebUrl("https://example.com/" + id);
        project.setCommits(new ArrayList<>());
        return project;
    }

    @BeforeEach
    void setUp() {
        restTemplate.postForEntity(baseUrl(), createProject("p1", "Sample Project"), Project.class);
    }

    @Test
    @DisplayName("GET /projects should return all projects")
    void getAllProjects() {
        ResponseEntity<Project[]> response = restTemplate.getForEntity(baseUrl(), Project[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    @DisplayName("GET /projects/{id} should return a project by ID")
    void getProjectById() {
        ResponseEntity<Project> response = restTemplate.getForEntity(baseUrl() + "/p1", Project.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("p1", response.getBody().getId());
        assertEquals("Sample Project", response.getBody().getName());
    }

    @Test
    @DisplayName("GET /projects/{id} should return 404 if not found")
    void getProjectByIdNotFound() {
        ResponseEntity<Project> response = restTemplate.getForEntity(baseUrl() + "/invalid-id", Project.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /projects should create a new project")
    void createProject() {
        Project newProject = createProject("p2", "New Project");
        ResponseEntity<Project> response = restTemplate.postForEntity(baseUrl(), newProject, Project.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("p2", response.getBody().getId());
        assertEquals("New Project", response.getBody().getName());
    }

    @Test
    @DisplayName("POST /projects should return 400 for invalid data")
    void createProjectValidationFails() {
        Project invalid = createProject("p3", "");
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), invalid, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /projects/{id} should delete existing project")
    void deleteProject() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/p1", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<Project> verify = restTemplate.getForEntity(baseUrl() + "/p1", Project.class);
        assertEquals(HttpStatus.NOT_FOUND, verify.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /projects/{id} should return 404 if not found")
    void deleteProjectNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/does-not-exist", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}