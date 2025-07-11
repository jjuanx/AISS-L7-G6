package aiss.gitminer.controller;

import aiss.gitminer.exception.UserNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.User;
import aiss.gitminer.repositories.CommentRepository;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "User", description = "User management API")
@RestController
@RequestMapping("/gitminer/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IssueRepository issueRepository;

    // GET http://localhost:8080/gitminer/users
    @Operation(
            summary = "Retrieve all Users",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }
            )
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/users/{id}
    @Operation(
            summary = "Retrieve a User by Id",
            description = "Get a User object by specifying its id",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping("/{id}")
    public User getUserById(@Parameter(description = "id of user to be searched", required = true)@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    // POST http://localhost:8080/gitminer/users
    @Operation(
            summary = "Create a User",
            description = "Create a User object by specifying its content",
            tags = {"create"}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        User newUser = new User(user.getId(), user.getUsername(), user.getName(), user.getAvatarUrl(), user.getWebUrl());
        return userRepository.save(newUser);
    }

    // DELETE http://localhost:8080/gitminer/user/{id}
    @Operation(
            summary = "Delete a User by Id",
            description = "Delete a User object by specifying its id",
            tags = {"delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteUser(@PathVariable String id) throws UserNotFoundException {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).get();
            
            // Delete all comments authored by this user
            List<Comment> comments = commentRepository.findByAuthorId(id, Pageable.unpaged()).getContent();
            for (Comment comment : comments) {
                commentRepository.delete(comment);
            }
            
            // Delete all issues authored by this user
            List<Issue> issues = issueRepository.findByAuthorId(id, Pageable.unpaged()).getContent();
            for (Issue issue : issues) {
                issueRepository.delete(issue);
            }
            
            userRepository.deleteById(id);
        }else {
            throw new UserNotFoundException();
        }
    }
}
