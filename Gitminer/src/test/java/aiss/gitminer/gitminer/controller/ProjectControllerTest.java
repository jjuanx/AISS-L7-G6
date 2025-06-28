package aiss.gitminer.controller;

import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Project sampleProject;

    @BeforeEach
    void setUp() {
        sampleProject = new Project();
        sampleProject.setId("p1");
        sampleProject.setName("Sample Project");
        sampleProject.setWebUrl("http://example.com/projects/p1");
    }

    @Test
    void getAllProjects() throws Exception {
        Mockito.when(projectRepository.findAll()).thenReturn(Collections.singletonList(sampleProject));

        mockMvc.perform(get("/gitminer/projects"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(sampleProject))));
    }

    @Test
    void getProjectById() throws Exception {
        Mockito.when(projectRepository.findById("p1")).thenReturn(Optional.of(sampleProject));

        mockMvc.perform(get("/gitminer/projects/p1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProject)));
    }

    @Test
    void getProjectByIdNotFound() throws Exception {
        Mockito.when(projectRepository.findById("invalid-id")).thenReturn(Optional.empty());

        mockMvc.perform(get("/gitminer/projects/invalid-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProject() throws Exception {
        Mockito.when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);

        mockMvc.perform(post("/gitminer/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProject)));
    }

    @Test
    void createProjectValidationFails() throws Exception {
        Project invalidProject = new Project();
        invalidProject.setId("p2");
        invalidProject.setName("");
        invalidProject.setWebUrl("http://example.com/projects/p2");

        mockMvc.perform(post("/gitminer/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProject)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProjectInternalServerError() throws Exception {
        Mockito.when(projectRepository.save(any(Project.class)))
                .thenThrow(new DataAccessException("Database error") {});

    }

    @Test
    void deleteProject() throws Exception {
        Mockito.when(projectRepository.existsById("p1")).thenReturn(true);

        mockMvc.perform(delete("/gitminer/projects/p1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProjectNotFound() throws Exception {
        Mockito.when(projectRepository.existsById("invalid-id")).thenReturn(false);

        mockMvc.perform(delete("/gitminer/projects/invalid-id"))
                .andExpect(status().isNotFound());
    }
}