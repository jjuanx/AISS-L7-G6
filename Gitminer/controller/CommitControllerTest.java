package aiss.gitminer.controller;

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
import java.util.Optional;

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
        Commit commit = new Commit("c1", "Init", "Initial commit", "2024-06-20T12:00:00Z", "author@example.com", "Alice", "https://repo/commit/c1");
        project.getCommits().add(commit);
        projectRepository.save(project);

        List<Commit> commits = commitController.getAllCommits();
        assertNotNull(commits);
        assertEquals(1, commits.size());
        assertEquals("Init", commits.get(0).getTitle());
    }

    @Test
    void getCommitById() throws CommitNotFoundException {
        Commit commit = new Commit("c2", "Fix", "Fixed bug", "2024-06-20T12:00:00Z", "bob@example.com", "Bob", "https://repo/commit/c2");
        project.getCommits().add(commit);
        projectRepository.save(project);

        Commit found = commitController.getCommitById("c2");
        assertNotNull(found);
        assertEquals("Fix", found.getTitle());
        assertEquals("Bob", found.getAuthorName());
    }

    @Test
    void getAllCommitsByProjectId() throws ProjectNotFoundException {
        Commit commit1 = new Commit("c3", "Refactor", "Refactored code", "2024-06-20T12:00:00Z", "dev@example.com", "Charlie", "https://repo/commit/c3");
        Commit commit2 = new Commit("c4", "Docs", "Updated docs", "2024-06-20T12:00:00Z", "doc@example.com", "Dana", "https://repo/commit/c4");
        project.getCommits().add(commit1);
        project.getCommits().add(commit2);
        projectRepository.save(project);

        List<Commit> commits = commitController.getAllCommitsByProjectId("test_project");
        assertEquals(2, commits.size());
    }

    @Test
    void createCommit() throws ProjectNotFoundException {
        Commit newCommit = new Commit("c5", "New Feature", "Added feature", "2024-06-20T12:00:00Z", "feature@example.com", "Eve", "https://repo/commit/c5");
        Commit saved = commitController.createCommit(newCommit, "test_project");

        assertNotNull(saved);
        assertEquals("c5", saved.getId());
        assertEquals("New Feature", saved.getTitle());

        List<Commit> commits = commitController.getAllCommitsByProjectId("test_project");
        assertEquals(1, commits.size());
    }

    @Test
    void deleteCommit() throws CommitNotFoundException {
        Commit commit = new Commit("c6", "Remove", "Removing stuff", "2024-06-20T12:00:00Z", "remove@example.com", "Frank", "https://repo/commit/c6");
        project.getCommits().add(commit);
        projectRepository.save(project);

        assertTrue(commitRepository.existsById("c6"));
        commitController.deleteCommit("c6");
        assertFalse(commitRepository.existsById("c6"));
    }

    @Test
    void updateCommit() throws CommitNotFoundException {
        Commit commit = new Commit("c7", "Old", "Old message", "2024-06-20T12:00:00Z", "old@example.com", "Grace", "https://repo/commit/c7");
        project.getCommits().add(commit);
        projectRepository.save(project);

        Commit updated = new Commit("c7", "Updated", "Updated message", "2024-06-21T10:00:00Z", "new@example.com", "Grace Hopper", "https://repo/commit/c7");

        commitController.updateCommit("c7", updated);

        Commit fetched = commitController.getCommitById("c7");
        assertEquals("Updated", fetched.getTitle());
        assertEquals("Updated message", fetched.getMessage());
        assertEquals("Grace Hopper", fetched.getAuthorName());
    }
}
