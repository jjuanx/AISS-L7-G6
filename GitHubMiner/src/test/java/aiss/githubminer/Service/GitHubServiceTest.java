package aiss.githubminer.Service;

import aiss.githubminer.exception.ExternalApiException;
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





    @Test
    @DisplayName("Throws ExternalApiException when project not found")
    void getProjectException() {
        String owner = "nonexistent-owner";
        String repo = "nonexistent-repo";

        Exception exception = assertThrows(ExternalApiException.class, () ->
                service.getProject(owner, repo)
        );

        String expectedMessage = "Error fetching GitHub project metadata";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Throws ExternalApiException when fetching commits from invalid repo")
    void getCommitsException() {
        String owner = "nonexistent-owner";
        String repo = "nonexistent-repo";

        Exception exception = assertThrows(ExternalApiException.class, () ->
                service.getCommits(owner, repo, 1, 1)
        );

        String expectedMessage = "Error encontrando commits";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Throws ExternalApiException when fetching issues from invalid repo")
    void getIssuesException() {
        String owner = "nonexistent-owner";
        String repo = "nonexistent-repo";

        Exception exception = assertThrows(ExternalApiException.class, () ->
                service.getIssues(owner, repo, 1, 1)
        );

        String expectedMessage = "Error encontrando issues";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Throws ExternalApiException when fetching comments from invalid repo")
    void getCommentsException() {
        String owner = "nonexistent-owner";
        String repo = "nonexistent-repo";

        Exception exception = assertThrows(ExternalApiException.class, () ->
                service.getComments(owner, repo, 1)
        );

        String expectedMessage = "Error encontrando comentarios";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("CreateProject builds a valid GMProject")
    void createProject() {
        String owner = "spring-projects";
        String repo = "spring-boot";
        GMProject project = service.createProject(owner, repo, 1, 1, 1);

        assertNotNull(project);
        assertNotNull(project.getId());
        assertEquals("spring-boot", project.getName());
        assertTrue(project.getCommits().size() > 0);
        assertTrue(project.getIssues().size() >= 0); // puede haber 0 issues recientes
    }

    @Test
    @DisplayName("Throws GitMinerPostException when failing to POST to GitMiner")
    void sendToGitMinerException() {
        GMProject dummyProject = new GMProject();
        dummyProject.setId("12345");
        dummyProject.setName("dummy-project");
        dummyProject.setWebUrl("http://invalid-url");

        // Cambia temporalmente la URL en el método si es necesario o provoca fallo DNS
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.sendToGitMiner(dummyProject);
        });

        assertTrue(exception.getMessage().contains("No se pudo enviar el proyecto a GitMiner")
                || exception.getMessage().contains("Connection refused"));
    }
}