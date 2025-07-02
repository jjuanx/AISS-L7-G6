package aiss.gitminer.gitminer.controller;

import aiss.gitminer.controller.CommitController;
import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.CommitRepository;
import aiss.gitminer.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommitControllerTest {

    @Autowired
    CommitController commitController;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    CommitRepository commitRepository;

    private Project project;

    @BeforeEach
    void setUp() {
        commitRepository.deleteAll();
        projectRepository.deleteAll();

        project = new Project();
        project.setId("test_project");
        project.setName("Test Project");
        project.setWebUrl("https://example.com");
        project.setCommits(new ArrayList<>());
        projectRepository.save(project);
    }

    @Test
    void getAllCommits() {
        Commit commit = new Commit("c1", "Title 1", "Message 1", "2024-06-20T12:00:00Z", "user1@example.com", "User One", "https://repo/commit/c1");
        project.getCommits().add(commit);
        projectRepository.save(project);

        List<Commit> commits = commitController.getAllCommits(0, 10, null, null);

        assertNotNull(commits);
        assertFalse(commits.isEmpty());
        assertEquals("Title 1", commits.get(0).getTitle());
    }

    @Test
    void getCommitById() throws CommitNotFoundException {
        Commit commit = new Commit("c2", "Title 2", "Message 2", "2024-06-20T12:00:00Z", "user2@example.com", "User Two", "https://repo/commit/c2");
        project.getCommits().add(commit);
        projectRepository.save(project);

        Commit found = commitController.getCommitById("c2");
        assertNotNull(found);
        assertEquals("Title 2", found.getTitle());
        assertEquals("User Two", found.getAuthorName());
    }

    @Test
    void getAllCommitsByProjectId() throws ProjectNotFoundException {
        Commit commit1 = new Commit("c3", "Title 3", "Message 3", "2024-06-20T12:00:00Z", "user3@example.com", "User Three", "https://repo/commit/c3");
        Commit commit2 = new Commit("c4", "Title 4", "Message 4", "2024-06-20T12:00:00Z", "user4@example.com", "User Four", "https://repo/commit/c4");
        project.getCommits().add(commit1);
        project.getCommits().add(commit2);
        projectRepository.save(project);

        List<Commit> commits = commitController.getAllCommitsByProjectId("test_project");
        assertEquals(2, commits.size());
    }

    @Test
    void createCommit() throws ProjectNotFoundException {
        Commit newCommit = new Commit("c5", "Title 5", "Message 5", "2024-06-20T12:00:00Z", "user5@example.com", "User Five", "https://repo/commit/c5");
        Commit saved = commitController.createCommit(newCommit, "test_project");

        assertNotNull(saved);
        assertEquals("c5", saved.getId());
        assertEquals("Title 5", saved.getTitle());

        List<Commit> commits = commitController.getAllCommitsByProjectId("test_project");
        assertEquals(1, commits.size());
    }

    @Test
    void deleteCommit() throws CommitNotFoundException {
        Commit commit = new Commit("c6", "Title 6", "Message 6", "2024-06-20T12:00:00Z", "user6@example.com", "User Six", "https://repo/commit/c6");
        project.getCommits().add(commit);
        projectRepository.save(project);

        assertTrue(commitRepository.existsById("c6"));
        commitController.deleteCommit("c6");
        assertFalse(commitRepository.existsById("c6"));
    }

    @Test
    void updateCommit() throws CommitNotFoundException {
        Commit commit = new Commit("c7", "Old Title", "Old Message", "2024-06-20T12:00:00Z", "user7@example.com", "User Seven", "https://repo/commit/c7");
        project.getCommits().add(commit);
        projectRepository.save(project);

        Commit updated = new Commit("c7", "New Title", "New Message", "2024-06-21T10:00:00Z", "user7@example.com", "User Seven Updated", "https://repo/commit/c7");
        commitController.updateCommit("c7", updated);

        Commit fetched = commitController.getCommitById("c7");
        assertEquals("New Title", fetched.getTitle());
        assertEquals("New Message", fetched.getMessage());
        assertEquals("User Seven Updated", fetched.getAuthorName());
    }
}