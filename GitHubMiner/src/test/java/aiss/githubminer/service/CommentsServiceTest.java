package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.DataModel.CommentData.CommentDatum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentsServiceTest {

    @Autowired
    CommentsService commentsService;

    @Test
    @DisplayName("Get Comments of a issues from a repo with maxPages")
    void getComments() {
        String url = "https://api.github.com/repos/spring-projects/spring-framework/issues/34812/comments";
        List<CommentDatum> comments = commentsService.getComments(url);
        assertNotNull(comments);
        assertFalse(comments.isEmpty());
        System.out.println(comments);
        System.out.println(comments.size());
    }
}