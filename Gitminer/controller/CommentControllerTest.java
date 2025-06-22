package aiss.gitminer.controller;

import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.User;
import aiss.gitminer.repositories.CommentRepository;
import aiss.gitminer.repositories.IssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IssueRepository issueRepository;

    private Comment comment;
    private Issue issue;

    @BeforeEach
    void setup() {
        commentRepository.deleteAll();
        issueRepository.deleteAll();

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("testuser");
        user.setName("Test User");
        user.setAvatarUrl("http://example.com/avatar.png");
        user.setWebUrl("http://example.com/user");

        comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setBody("This is a test comment.");
        comment.setAuthor(user);
        comment.setCreatedAt("2023-01-01");

        issue = new Issue();
        issue.setId(UUID.randomUUID().toString());
        issue.setTitle("Test Issue");
        issue.setComments(Collections.singletonList(comment));

        issueRepository.save(issue);
        commentRepository.save(comment);
    }

    @Test
    void getAllComments() throws Exception {
        mockMvc.perform(get("/gitminer/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body").value("This is a test comment."));
    }

    @Test
    void getCommentById() throws Exception {
        mockMvc.perform(get("/gitminer/comments/" + comment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()));
    }

    @Test
    void getCommentsByIssue() throws Exception {
        mockMvc.perform(get("/gitminer/issues/" + issue.getId() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(comment.getId()));
    }

    @Test
    void createComment() throws Exception {
        String json = "{ \"id\": \"" + UUID.randomUUID() + "\", " +
                "\"body\": \"Another comment.\", " +
                "\"author\": { \"id\": \"u2\", \"username\": \"anotheruser\", \"name\": \"Another\", \"avatar_url\": \"http://avatar.com\", \"web_url\": \"http://web.com\" }, " +
                "\"created_at\": \"2023-01-02\" }";

        mockMvc.perform(post("/gitminer/issues/" + issue.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body").value("Another comment."));
    }

    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/gitminer/comments/" + comment.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateComment() throws Exception {
        String json = "{ \"id\": \"" + comment.getId() + "\", " +
                "\"body\": \"Updated comment.\", " +
                "\"author\": { \"id\": \"" + comment.getAuthor().getId() + "\", \"username\": \"testuser\", \"name\": \"Test User\", \"avatar_url\": \"http://avatar.com\", \"web_url\": \"http://web.com\" }, " +
                "\"created_at\": \"" + comment.getCreatedAt() + "\", " +
                "\"updated_at\": \"\" }";

        mockMvc.perform(put("/gitminer/comments/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }
}
