package aiss.gitminer.gitminer.controller;

import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/gitminer";
    }

    private String userId;
    private String issueId;
    private String commentId;

    @BeforeEach
    void setUp() {
        // Crear usuario
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("commentUser");
        user.setName("Comment Tester");
        user.setAvatarUrl("https://example.com/avatar");
        user.setWebUrl("https://example.com/user");

        ResponseEntity<User> userResponse = restTemplate.postForEntity(baseUrl() + "/users", user, User.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        userId = userResponse.getBody().getId();

        // Crear proyecto
        Project project = new Project();
        project.setId("comment-proj");
        project.setName("Project for Comments");
        project.setWebUrl("https://example.com/project");
        project.setCommits(new ArrayList<>());

        ResponseEntity<Project> projResponse = restTemplate.postForEntity(baseUrl() + "/projects", project, Project.class);
        assertEquals(HttpStatus.CREATED, projResponse.getStatusCode());

        // Crear issue
        Issue issue = new Issue();
        issue.setId(UUID.randomUUID().toString());
        issue.setTitle("Issue with comments");
        issue.setDescription("Testing comments");
        issue.setState("open");
        issue.setLabels(Collections.emptyList());
        issue.setAuthor(user);
        issue.setComments(Collections.emptyList());

        ResponseEntity<Issue> issueResponse = restTemplate.postForEntity(
                baseUrl() + "/projects/comment-proj/issues",
                issue,
                Issue.class
        );
        assertEquals(HttpStatus.CREATED, issueResponse.getStatusCode());
        issueId = issueResponse.getBody().getId();

        // Crear comment
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setBody("Comentario inicial");
        comment.setAuthor(user);
        comment.setCreatedAt("2025-07-01");

        ResponseEntity<Comment> commentResponse = restTemplate.postForEntity(
                baseUrl() + "/issues/" + issueId + "/comments",
                comment,
                Comment.class
        );
        assertEquals(HttpStatus.CREATED, commentResponse.getStatusCode());
        commentId = commentResponse.getBody().getId();
    }

    @Test
    @DisplayName("GET /comments devuelve todos los comentarios")
    void getAllComments() {
        ResponseEntity<Comment[]> response = restTemplate.getForEntity(baseUrl() + "/comments", Comment[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    @DisplayName("GET /comments/{id} devuelve un comentario concreto")
    void getCommentById() {
        ResponseEntity<Comment> response = restTemplate.getForEntity(
                baseUrl() + "/comments/" + commentId,
                Comment.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(commentId, response.getBody().getId());
    }

    @Test
    @DisplayName("GET /issues/{id}/comments devuelve comentarios del issue")
    void getCommentsByIssue() {
        ResponseEntity<Comment[]> response = restTemplate.getForEntity(
                baseUrl() + "/issues/" + issueId + "/comments",
                Comment[].class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    @DisplayName("POST /issues/{id}/comments crea un nuevo comentario")
    void createComment() {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setBody("Nuevo comentario de prueba");
        comment.setAuthor(new User(userId, "commentUser", "Comment Tester", "https://example.com/avatar", "https://example.com/user"));
        comment.setCreatedAt("2025-07-02");

        ResponseEntity<Comment> response = restTemplate.postForEntity(
                baseUrl() + "/issues/" + issueId + "/comments",
                comment,
                Comment.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nuevo comentario de prueba", response.getBody().getBody());
    }

    @Test
    @DisplayName("PUT /comments/{id} actualiza un comentario")
    void updateComment() {
        Comment updated = new Comment();
        updated.setId(commentId);
        updated.setBody("Comentario actualizado");
        updated.setAuthor(new User(userId, "commentUser", "Comment Tester", "https://example.com/avatar", "https://example.com/user"));
        updated.setCreatedAt("2025-07-01");
        updated.setUpdatedAt("2025-07-03");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Comment> request = new HttpEntity<>(updated, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/comments/" + commentId,
                HttpMethod.PUT,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /comments/{id} elimina un comentario")
    void deleteComment() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/comments/" + commentId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verifica que ya no existe
        ResponseEntity<Comment> check = restTemplate.getForEntity(
                baseUrl() + "/comments/" + commentId,
                Comment.class
        );
        assertEquals(HttpStatus.NOT_FOUND, check.getStatusCode());
    }
}