package aiss.githubminer.Service;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.gitminer.GMProject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GitHubServiceTest {

    @Autowired
    GitHubService service;

    @Test
    @DisplayName("Get the project from a owner")
    void getProject() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        GMProject project = service.getProject(owner, repo);
        assertNotNull(project);
        assertEquals("spring-boot", project.getName());
        System.out.println(project.toString());
    }

    @Test
    @DisplayName("Get the commits of a repo with maxPages && sinceCommits")
    void getCommits() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        Integer maxPages = 10;
        Integer sinceCommits = 30;
        List<Commit> commits = service.getCommits(owner,repo, maxPages, sinceCommits);
        assertNotNull(commits);
        assertTrue(commits.size() > 0);
        System.out.println(commits.toString());
    }

    @Test
    @DisplayName("Get issues of a repo with maxPages && sinceCommits")
    void getIssues() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        Integer maxPages = 10;
        Integer sinceIssues = 30;
        List<Issue> issues = service.getIssues(owner,repo, maxPages, sinceIssues);
        assertNotNull(issues);
        assertTrue(issues.size() > 0);
        System.out.println(issues.toString());
    }

    @Test
    @DisplayName("Get Comments of a issues from a repo with maxPages")
    void getComments() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        List<Comment> comments = service.getComments(owner,repo, 40);
        assertNotNull(comments);
        assertTrue(comments.size() > 0);
        System.out.println(comments.toString());
    }

    @Test
    void testGetCommits() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        List<Commit> commits = service.getCommits(owner,repo);
        assertNotNull(commits);
        assertTrue(commits.size() > 0);
        System.out.println(commits.toString());
    }

    @Test
    void testGetIssues() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        List<Issue> issues = service.getIssues(owner,repo);
        assertNotNull(issues);
        assertTrue(issues.size() > 0);
        System.out.println(issues.toString());
    }

    @Test
    void testGetComments() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        List<Comment> comments = service.getComments(owner,repo);
        assertNotNull(comments);
        assertTrue(comments.size() > 0);
        System.out.println(comments.toString());
    }
}