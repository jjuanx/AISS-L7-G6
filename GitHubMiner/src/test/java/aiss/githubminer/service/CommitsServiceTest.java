package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.DataModel.CommitData.CommitDatum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommitsServiceTest {

    @Autowired
    CommitsService commitsService;

    @Test
    @DisplayName("Get the commits of a repo with maxPages && sinceCommits")
    void getCommits() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        Integer maxPages = 10;
        Integer sinceCommits = 30;
        List<CommitDatum> commits = commitsService.getCommits(owner,repo, maxPages, sinceCommits);
        assertNotNull(commits);
        assertFalse(commits.isEmpty());
        assertTrue(commits.size() > 0);
        System.out.println(commits.toString());
    }

    @Test
    @DisplayName("Throws ExternalApiException when fetching commits from invalid repo")
    void getCommitsException() {
        String owner = "nonexistent-owner";
        String repo = "nonexistent-repo";

        Exception exception = assertThrows(ExternalApiException.class, () ->
                commitsService.getCommits(owner, repo, 1, 1)
        );

        String expectedMessage = "Error encontrando commits";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}