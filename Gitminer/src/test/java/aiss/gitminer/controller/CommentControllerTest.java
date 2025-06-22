package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private String getBaseUrl() {
        return "http://localhost:" + port + "/gitminer/comments";
    }

    private String getIssueBaseUrl() {
        return "http://localhost:" + port + "/gitminer/issues";
    }

    @Test
    @DisplayName("GET /comments retrieves all comments")
    void getAllComments() {
        Comment comment = new Comment(UUID.randomUUID().toString(), "Test comment body");
        restTemplate.postForEntity(getBaseUrl(), comment, Comment.class);
        ResponseEntity<List<Comment>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "The response body should not be null");
        assertFalse(response.getBody().isEmpty(), "The list of comments should not be empty");
        assertTrue(response.getBody().stream().allMatch(
                c -> c.getId() != null && !c.getId().isEmpty() &&
                        c.getBody() != null && !c.getBody().isEmpty()
        ), "Each comment should have valid fields.");
    }


    @Test
    @DisplayName("GET /comments/{id} retrieves comment by ID")
    void getCommentById() {
        String commentId = "1"; // Assuming this comment exists in DB or has been preloaded
        ResponseEntity<Comment> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + commentId,
                Comment.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(commentId, response.getBody().getId());
    }

    @Test
    @DisplayName("GET /comments/{id} throws CommentNotFoundException for invalid ID")
    void getCommentByIdThrowsException() {
        String invalidId = "999"; // Assuming this ID does not exist
        ResponseEntity<Comment> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + invalidId,
                Comment.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return 404 NOT FOUND");
    }

    @Test
    @DisplayName("GET /issues/{issueId}/comments retrieves comments by issue ID")
    void getCommentsByIssue() {
        String issueId = "101"; // Assuming this issue exists in DB or has been preloaded
        ResponseEntity<List<Comment>> response = restTemplate.exchange(
                getIssueBaseUrl() + "/" + issueId + "/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty(), "The comments for the issue should not be empty");
    }

    @Test
    @DisplayName("GET /issues/{issueId}/comments throws IssueNotFoundException for invalid issue ID")
    void getCommentsByIssueThrowsException() {
        String invalidIssueId = "999"; // Assuming this issue ID does not exist
        ResponseEntity<List<Comment>> response = restTemplate.exchange(
                getIssueBaseUrl() + "/" + invalidIssueId + "/comments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return 404 NOT FOUND");
    }

    @Test
    @DisplayName("POST /issues/{issueId}/comments creates a new comment")
    void createComment() {
        String issueId = "101"; // Assuming this issue exists
        Comment newComment = new Comment(null, "This is a new comment body");

        ResponseEntity<Comment> response = restTemplate.postForEntity(
                getIssueBaseUrl() + "/" + issueId + "/comments",
                newComment,
                Comment.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newComment.getBody(), response.getBody().getBody());
    }

    @Test
    @DisplayName("POST /issues/{issueId}/comments throws IssueNotFoundException for invalid issue")
    void createCommentThrowsException() {
        String invalidIssueId = "999"; // Assuming this issue does not exist
        Comment newComment = new Comment(null, "Another comment body");

        ResponseEntity<Comment> response = restTemplate.postForEntity(
                getIssueBaseUrl() + "/" + invalidIssueId + "/comments",
                newComment,
                Comment.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return 404 NOT FOUND");
    }

    @Test
    @DisplayName("DELETE /comments/{id} deletes a comment by ID")
    void deleteComment() {
        String commentId = "1"; // Assuming this comment exists in DB
        restTemplate.delete(getBaseUrl() + "/" + commentId);

        ResponseEntity<Comment> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + commentId,
                Comment.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "The comment should no longer exist");
    }

    @Test
    @DisplayName("DELETE /comments/{id} throws CommentNotFoundException for invalid ID")
    void deleteCommentThrowsException() {
        String invalidId = "999"; // Assuming this ID does not exist
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + invalidId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return 404 NOT FOUND");
    }

    @Test
    @DisplayName("PUT /comments/{id} updates a comment by ID")
    void updateComment() {
        String commentId = "2"; // Assuming this comment exists
        Comment updatedComment = new Comment(commentId, "Updated comment body");

        restTemplate.put(
                getBaseUrl() + "/" + commentId,
                updatedComment
        );

        ResponseEntity<Comment> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + commentId,
                Comment.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedComment.getBody(), response.getBody().getBody());
    }

    @Test
    @DisplayName("PUT /comments/{id} throws CommentNotFoundException for invalid ID")
    void updateCommentThrowsException() {
        String invalidId = "999"; // Assuming this ID does not exist
        Comment updatedComment = new Comment(invalidId, "Updated comment");

        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + invalidId,
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return 404 NOT FOUND");
    }
}
