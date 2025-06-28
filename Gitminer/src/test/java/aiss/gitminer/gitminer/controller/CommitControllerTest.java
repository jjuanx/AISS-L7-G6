package aiss.gitminer.controller;

import aiss.gitminer.model.Commit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.DELETE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommitControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/gitminer";
    }

    @Test
    @DisplayName("GET /commits retrieves all commits")
    void getAllCommits() {
        ResponseEntity<Commit[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/commits",
                Commit[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    @DisplayName("GET /commits/{id} retrieves a specific commit by ID")
    void getCommitById() {
        // Configurar un ID de commit válido
        String validCommitId = "12345";

        // Realizar la solicitud GET
        ResponseEntity<Commit> response = restTemplate.getForEntity(getBaseUrl() + "/commits/" + validCommitId, Commit.class);

        // Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validCommitId, response.getBody().getId());
        assertNotNull(response.getBody().getMessage(), "El mensaje del commit no debe ser nulo");
    }

    @Test
    @DisplayName("GET /commits/{id} throws CommitNotFoundException for invalid ID")
    void getCommitByIdThrowsException() {
        String invalidCommitId = "invalid-id";

        ResponseEntity<Commit> response = restTemplate.getForEntity(
                getBaseUrl() + "/commits/" + invalidCommitId,
                Commit.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    @DisplayName("GET /projects/{projectId}/commits retrieves all commits by project ID")
    void getAllCommitsByProjectId() {
        String projectId = "known-project-id";

        ResponseEntity<Commit[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/projects/" + projectId + "/commits",
                Commit[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("GET /projects/{projectId}/commits throws ProjectNotFoundException for invalid project ID")
    void getAllCommitsByProjectIdThrowsException() {
        String invalidProjectId = UUID.randomUUID().toString();

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/projects/" + invalidProjectId + "/commits",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Project not found"));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/commits creates a new commit")
    void createCommit() {
        String projectId = "known-project-id";

        Commit newCommit = new Commit(
                "Sample Commit Title",
                "Sample commit message",
                "2023-10-10",
                "author@example.com",
                "Author Name",
                "http://example.com/commit"
        );

        ResponseEntity<Commit> response = restTemplate.postForEntity(
                getBaseUrl() + "/projects/" + projectId + "/commits",
                newCommit,
                Commit.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newCommit.getTitle(), response.getBody().getTitle());
    }

    @Test
    @DisplayName("POST /projects/{projectId}/commits throws ProjectNotFoundException for invalid project ID")
    void createCommitThrowsException() {
        String invalidProjectId = UUID.randomUUID().toString();

        Commit newCommit = new Commit(
                "Sample Commit Title",
                "Sample commit message",
                "2023-10-10",
                "author@example.com",
                "Author Name",
                "http://example.com/commit"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/projects/" + invalidProjectId + "/commits",
                newCommit,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Project not found"));
    }

    @Test
    @DisplayName("DELETE /commits/{id} deletes a commit")
    void deleteCommit() {
        String commitId = "known-commit-id";

        restTemplate.delete(getBaseUrl() + "/commits/" + commitId);

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/commits/" + commitId,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /commits/{id} throws CommitNotFoundException for invalid ID")
    void deleteCommitThrowsException() {
        String invalidCommitId = UUID.randomUUID().toString();

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/commits/" + invalidCommitId,
                DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Commit not found"));
    }
}