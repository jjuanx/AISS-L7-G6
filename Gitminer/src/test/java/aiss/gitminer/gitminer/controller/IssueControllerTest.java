package aiss.gitminer.gitminer.controller;

import aiss.gitminer.controller.IssueController;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IssueController.class)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueRepository issueRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Issue sampleIssue;

    @BeforeEach
    void setUp() {
        sampleIssue = new Issue("Sample Title", "Sample description", "open", Collections.emptyList(), "123", 0, null, Collections.emptyList());
    }

    @Test
    void getAllIssues() throws Exception {
        Page<Issue> mockPage = new PageImpl<>(Collections.singletonList(sampleIssue));
        Mockito.when(issueRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(mockPage);

        mockMvc.perform(get("/gitminer/issues"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(sampleIssue.getId()))
                .andExpect(jsonPath("$[0].title").value(sampleIssue.getTitle()));
    }

    @Test
    void getIssueById() throws Exception {
        // Configuración del mock
        Mockito.when(issueRepository.findById("123")).thenReturn(Optional.of(sampleIssue));

        // Solicitud y verificación
        mockMvc.perform(get("/gitminer/issues/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sampleIssue.getId()))
                .andExpect(jsonPath("$.title").value(sampleIssue.getTitle()));
    }

    @Test
    void getIssueByIdNotFound() throws Exception {
        // Configuración del mock
        Mockito.when(issueRepository.findById("404")).thenReturn(Optional.empty());

        // Solicitud y verificación
        mockMvc.perform(get("/gitminer/issues/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllIssuesByProjectIdNotFound() throws Exception {
        // Configuración del mock
        Mockito.when(projectRepository.findById("invalid-project")).thenReturn(Optional.empty());

        // Solicitud y verificación
        mockMvc.perform(get("/gitminer/projects/invalid-project/issues"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createIssue() throws Exception {
        // Mock del proyecto
        Project mockProject = new Project();
        mockProject.setId("project1");
        mockProject.setIssues(new ArrayList<>());

        // Configuración del mock
        Mockito.when(projectRepository.findById("project1")).thenReturn(Optional.of(mockProject));
        Mockito.when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

        // Solicitud y verificación
        mockMvc.perform(post("/gitminer/projects/project1/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleIssue)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(sampleIssue.getTitle()));
    }

    @Test
    void createIssueProjectNotFound() throws Exception {
        // Configuración del mock
        Mockito.when(projectRepository.findById("invalid-project")).thenReturn(Optional.empty());

        // Solicitud y verificación
        mockMvc.perform(post("/gitminer/projects/invalid-project/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleIssue)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateIssue() throws Exception {
        // Mock del issue existente
        Issue existingIssue = new Issue("oldTitle", "oldDescription", "open",
                Collections.emptyList(), "123", 0, null, Collections.emptyList());

        // Mock del issue actualizado
        Issue updatedIssue = new Issue("newTitle", "newDescription", "closed",
                Collections.emptyList(), "123", 0, null, Collections.emptyList());

        // Configuración de los mocks
        Mockito.when(issueRepository.findById("123")).thenReturn(Optional.of(existingIssue));
        Mockito.when(issueRepository.save(Mockito.any(Issue.class))).thenReturn(updatedIssue);

        // Solicitud HTTP con el contenido del issue actualizado
        mockMvc.perform(put("/gitminer/issues/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedIssue)))
                .andExpect(status().isNoContent());

        // Verificación adicional de interacción del repositorio
        Mockito.verify(issueRepository).findById("123");
        Mockito.verify(issueRepository).save(Mockito.any(Issue.class));
    }

    @Test
    void updateIssueNotFound() throws Exception {
        // Configuración del mock
        Mockito.when(issueRepository.findById("404")).thenReturn(Optional.empty());

        // Solicitud y verificación
        mockMvc.perform(put("/gitminer/issues/404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleIssue)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteIssue() throws Exception {
        // Configuración del mock
        Mockito.when(issueRepository.existsById("123")).thenReturn(true);
        Mockito.doNothing().when(issueRepository).deleteById("123");

        mockMvc.perform(delete("/gitminer/issues/123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteIssueNotFound() throws Exception {
        // Configuración del mock
        Mockito.when(issueRepository.existsById("404")).thenReturn(false);

        // Solicitud y verificación
        mockMvc.perform(delete("/gitminer/issues/404"))
                .andExpect(status().isNotFound());
    }
}
