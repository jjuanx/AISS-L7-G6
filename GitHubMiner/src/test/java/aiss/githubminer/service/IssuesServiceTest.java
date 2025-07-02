package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.DataModel.IssueData.IssueDatum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IssuesServiceTest {

    @Autowired
    IssuesService issuesService;

    @Test
    @DisplayName("Get issues of a repo with maxPages && sinceCommits")
    void getIssues() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        Integer maxPages = 10;
        Integer sinceIssues = 30;
        List<IssueDatum> issues = issuesService.getIssues(owner,repo, maxPages, sinceIssues);
        assertNotNull(issues);
        assertFalse(issues.isEmpty());
        assertTrue(issues.size() > 0);
        System.out.println(issues.toString());
    }


    @Test
    @DisplayName("Throws ExternalApiException when fetching issues from invalid repo")
    void getIssuesException() {
        String owner = "nonexistent-owner";
        String repo = "nonexistent-repo";

        Exception exception = assertThrows(ExternalApiException.class, () ->
                issuesService.getIssues(owner, repo, 1, 1)
        );

        String expectedMessage = "Error encontrando issues";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}