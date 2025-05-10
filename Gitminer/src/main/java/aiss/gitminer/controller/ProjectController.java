package aiss.gitminer.controller;

import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import aiss.gitminer.repositories.ProjectRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;

    // GET http://localhost:8080/gitminer/projects
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/projects/{id}
    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable String id) throws ProjectNotFoundException {
        Optional<Project> project =  projectRepository.findById(id);
        if(!project.isPresent()){
            throw new ProjectNotFoundException();
        }
        return project.get();
    }

    // POST http://localhost:8080/gitminer/projects
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project createProject(@RequestBody @Valid Project project) {
        //Project newProject = new Project();
        //newProject.setId(UUID.randomUUID().toString());
        //newProject.setName(project.getName());
        //newProject.setWebUrl(project.getWebUrl());

        return projectRepository.save(project);
    }

    // DELETE http://localhost:8080/gitminer/projects/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable String id) throws ProjectNotFoundException {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }else{
            throw new ProjectNotFoundException();
        }
    }

}
