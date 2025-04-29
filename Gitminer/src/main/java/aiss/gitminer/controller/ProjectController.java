package aiss.gitminer.controller;

import aiss.gitminer.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import aiss.gitminer.repositories.ProjectRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable String id) {
        Optional<Project> project =  projectRepository.findById(id);
        return project.orElse(null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project createProject(@RequestBody @Valid Project project) {
        Project newProject = new Project();
        newProject.setName(project.getName());
        newProject.setWebUrl(project.getWebUrl());

        return projectRepository.save(newProject);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable String id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }
    }

}
