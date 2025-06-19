package aiss.gitminer.controller;

import aiss.gitminer.exception.ProjectNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import aiss.gitminer.repositories.ProjectRepository;

import javax.validation.Valid;
import java.util.ArrayList;
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of projects",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Project.class))
            )
    })
    @GetMapping
    public List<Project> getAllProjects(
            @Parameter(description = "Page number to be retrieved") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of projects per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "attribute to be filtered") @RequestParam(defaultValue = "10") String name,
            @Parameter(description = "order of the request retrieved") @RequestParam(defaultValue = "10") String order
    ) {
        Pageable paging;

        if(order != null) {
            if (order.startsWith("-")) {
                paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
            } else {
                paging = PageRequest.of(page, size, Sort.by(order).ascending());
            }
        }else {
            paging = PageRequest.of(page, size);
        }
        Page<Project> pageProjects;
        if (name != null)
            pageProjects = projectRepository.findByName(name, paging);
        else
            pageProjects = projectRepository.findAll(paging);
        return pageProjects.getContent();
    }

    // GET http://localhost:8080/gitminer/projects/{id}
    @Operation(
            summary = "Retrieve a Project by Id",
            description = "Get a Project object by specifying its id",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Project.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public Project getProjectById(@Parameter(description = "id of project to be searched") @PathVariable String id) throws ProjectNotFoundException {
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Project.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project createProject(
            @Parameter(description = "Project object to be created", required = true)
            @RequestBody @Valid Project project) {

        List<Commit> commits = project.getCommits() != null ? project.getCommits() : new ArrayList<>();
        List<Issue> issues = project.getIssues() != null ? project.getIssues() : new ArrayList<>();
        Project newProject = new Project(project.getId(), project.getName(), project.getWebUrl(),commits,issues);
        return projectRepository.save(newProject);
    }

    // DELETE http://localhost:8080/gitminer/projects/{id}
    @Operation(
            summary = "Delete a Project by Id",
            description = "Delete a Project object by specifying its id",
            tags = {"delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Project successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteProject(@Parameter(description = "id of project to be searched") @PathVariable String id) throws ProjectNotFoundException {
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Project successfully updated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateProject(
            @Parameter(description = "id of project to be updated")
            @PathVariable String id,
            @Parameter(description = "Project data to update the existing project")
            @RequestBody @Valid Project project
    ) throws ProjectNotFoundException {
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
