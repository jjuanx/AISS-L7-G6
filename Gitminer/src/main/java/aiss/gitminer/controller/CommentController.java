package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.repositories.CommentRepository;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "Comment", description = "Comment management API")
@RestController
@RequestMapping("/gitminer")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IssueRepository issueRepository;

    // GET http://localhost:8080/gitminer/comments
    @Operation(
            summary = "Retrieve all Comments",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of comments",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }
            )
    })
    @GetMapping("/comments")
    public List<Comment> getAllComments (){
        return commentRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/comments/{id}
    @Operation(
            summary = "Retrieve a Comment by Id",
            description = "Get a Comment object by specifying its id",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping("/comments/{id}")
    public Comment getCommentById(@Parameter(description = "id of comment to be searched", required = true) @PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if(!comment.isPresent()){
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    // GET http://localhost:8080/gitminer/issues/{issueId}/comments
    @Operation(
            summary = "Retrieve all Comments by Issue Id",
            description = "Get all Comments by specifying its issue id",
            tags = {"get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comments found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Issue not found",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping("/issues/{issueId}/comments")
    public List<Comment> getCommentsByIssue(@Parameter(description = "id of the issue whose comments are to be retrieved", required = true) @PathVariable String issueId) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if(!issue.isPresent()){
            throw new IssueNotFoundException();
        }
        List<Comment> comments = new ArrayList<>(issue.get().getComments());
        return comments;
    }

    // POST http://localhost:8080/gitminer/issues/{issueId}/comments
    @Operation(
            summary = "Create a Comment",
            description = "Create a Comment object by specifying its content and his Issue id",
            tags = {"create"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment successfully created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Issue not found",
                    content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/issues/{issueId}/comments")
    public Comment createComment(
            @Parameter(description = "Comment object to be created", required = true)
            @RequestBody @Valid Comment comment,
            @Parameter(description = "id of the issue to associate the comment with", required = true)
            @PathVariable String issueId
    ) throws IssueNotFoundException {
        Optional < Issue> issue = issueRepository.findById(issueId);
        if (!issue.isPresent()) {
            throw new IssueNotFoundException();
        }
        Comment newComment = new Comment();
        newComment.setId(comment.getId());
        newComment.setBody(comment.getBody());
        newComment.setAuthor(comment.getAuthor());
        issue.get().getComments().add(newComment);
        issueRepository.save(issue.get());
        return newComment;
    }

    // DELETE http://localhost:8080/gitminer/comments/{id}
    @Operation(
            summary = "Delete a Comment by Id",
            description = "Delete a Comment object by specifying its id",
            tags = {"delete"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Comment successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{id}")
    public void deleteComment(@Parameter(description = "id of comment to be deleted", required = true) @PathVariable String id) throws CommentNotFoundException {
        if(commentRepository.existsById(id)) {
            Comment comment = commentRepository.findById(id).get();
            comment.setAuthor(null);
            commentRepository.save(comment);
            commentRepository.deleteById(id);
        }else{
            throw new CommentNotFoundException();
        }
    }

    // PUT http://localhost:8080/gitminer/comments/{id}
    @Operation(
            summary = "Update a Comment by Id",
            description = "Update a Comment object by specifying its id",
            tags = {"update"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Comment successfully updated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema())
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/comments/{id}")
    public void updateComment(
            @Parameter(description = "Comment object to be updated", required = true)
            @RequestBody @Valid Comment comment,
            @Parameter(description = "id of comment to be updated", required = true)
            @PathVariable String id
    ) throws CommentNotFoundException {
        Optional<Comment> commenData = commentRepository.findById(id);
        if (!commenData.isPresent()) {
            throw new CommentNotFoundException();
        }
        Comment _comment = commenData.get();
        _comment.setBody(comment.getBody());
        _comment.setUpdatedAt(LocalDateTime.now().toString());
        commentRepository.save(_comment);
    }
}
