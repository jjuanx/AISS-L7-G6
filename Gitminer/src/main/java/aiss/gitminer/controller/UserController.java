package aiss.gitminer.controller;

import aiss.gitminer.exception.UserNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.User;
import aiss.gitminer.repositories.CommentRepository;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) throws UserNotFoundException {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).get();
            List<Issue> issues = issueRepository.findAll()
                    .stream()
                    .filter(i -> i.getAuthor() != null && i.getAuthor().getId().equals(id))
                    .toList();
            for (Issue issue : issues) {
                issueRepository.delete(issue);
            }
            userRepository.deleteById(id);
        }else {
            throw new UserNotFoundException();
        }
    }
}
