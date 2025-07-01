package aiss.githubminer.service;

import aiss.githubminer.exception.ExternalApiException;
import aiss.githubminer.model.DataModel.ProjectData;
import aiss.githubminer.model.GitMinerModel.ProjectGitMiner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    ProjectService projectService;

    @Test
    @DisplayName("Get the project from a owner")
    void getProject() {
        String owner = "spring-projects";
        String repo = "spring-framework";
        ProjectData project = projectService.getProject(owner, repo);
        assertNotNull(project);
        System.out.println(project);
    }

    @Test
    @DisplayName("Throws ExternalApiException when project not found")
    void getProjectException() {
        String owner = "nonexistent-owner";
        String repo = "nonexistent-repo";

        Exception exception = assertThrows(ExternalApiException.class, () ->
                projectService.getProject(owner, repo)
        );

        String expectedMessage = "Error fetching GitHub project metadata";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Throws GitMinerPostException when failing to POST to GitMiner")
    void sendToGitMinerException() {
        ProjectGitMiner dummyProject = new ProjectGitMiner();
        dummyProject.setId("12345");
        dummyProject.setName("dummy-project");
        dummyProject.setWebUrl("http://invalid-url");

        // Cambia temporalmente la URL en el método si es necesario o provoca fallo DNS
        Exception exception = assertThrows(RuntimeException.class, () -> {
            projectService.sendToGitMiner(dummyProject);
        });

        assertTrue(exception.getMessage().contains("No se pudo enviar el proyecto a GitMiner")
                || exception.getMessage().contains("Connection refused"));
    }

    @Test
    void getProjectMiner() {
        String owner = "spring-projects";
        String repo = "spring-framework";
        ProjectGitMiner project = projectService.createProject(owner, repo,5,30,2);
        System.out.println(project);
        System.out.println(project.getCommits().size());
        System.out.println(project.getCommits().get(0));
        System.out.println(project.getIssues().size());
        System.out.println(project.getIssues().get(0).getComments().size());
        System.out.println(project.getIssues().get(0).getAuthor());
    }
}