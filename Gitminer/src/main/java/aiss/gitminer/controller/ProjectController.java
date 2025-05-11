package aiss.gitminer.controller;

import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Project;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import aiss.gitminer.repositories.ProjectRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Project", description = "Project management API")
@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;

    // GET http://localhost:8080/gitminer/projects
    @Operation(
            summary = "Retrieve all Projects",
            tags = {"get"}
    )
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/projects/{id}
    @Operation(
            summary = "Retrieve a Project by Id",
            description = "Get a Project object by specifying its id",
            tags = {"get"}
    )
    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable String id) throws ProjectNotFoundException {
        Optional<Project> project =  projectRepository.findById(id);
        if(!project.isPresent()){
            throw new ProjectNotFoundException();
        }
        return project.get();
    }

    // POST http://localhost:8080/gitminer/projects
    @Operation(
            summary = "Create a Project",
            description = "Create a Project object by specifying its content",
            tags = {"create"}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project createProject(@RequestBody @Valid Project project) {
        Project newProject = new Project();

        newProject.setId(project.getId());
        newProject.setName(project.getName());
        newProject.setWebUrl(project.getWebUrl());

        return projectRepository.save(newProject);
    }

    // DELETE http://localhost:8080/gitminer/projects/{id}
    @Operation(
            summary = "Delete a Project by Id",
            description = "Delete a Project object by specifying its id",
            tags = {"delete"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable String id) throws ProjectNotFoundException {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }else{
            throw new ProjectNotFoundException();
        }
    }

    // PUT http://localhost:8080/gitminer/projects/{id}
    @Operation(
            summary = "Update a Project by Id",
            description = "Update a Project object by specifying its id",
            tags = {"update"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateProject(@PathVariable String id, @RequestBody @Valid Project project) throws ProjectNotFoundException {
        Optional <Project> projectData = projectRepository.findById(id);
        if(!projectData.isPresent()){
            throw new ProjectNotFoundException();
        }
        Project newProject = projectData.get();
        newProject.setName(project.getName());
        newProject.setWebUrl(project.getWebUrl());
        projectRepository.save(newProject);
    }
}
