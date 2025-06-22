package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.exception.IssueNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.repositories.CommentRepository;
import aiss.gitminer.repositories.IssueRepository;
import aiss.gitminer.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IssueRepository issueRepository;

    // GET http://localhost:8080/gitminer/comments
    @GetMapping("/comments")
    public List<Comment> getAllComments (){
        return commentRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/comments/{id}
    @GetMapping("/comments/{id}")
    public Comment getCommentById(@PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if(!comment.isPresent()){
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    // GET http://localhost:8080/gitminer/issues/{issueId}/comments
    @GetMapping("/issues/{issueId}/comments")
    public List<Comment> getCommentsByIssue(@PathVariable String issueId) throws IssueNotFoundException {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if(!issue.isPresent()){
            throw new IssueNotFoundException();
        }
        List<Comment> comments = new ArrayList<>(issue.get().getComments());
        return comments;
    }

    // POST http://localhost:8080/gitminer/issues/{issueId}/comments
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/issues/{issueId}/comments")
    public Comment createComment(@RequestBody @Valid Comment comment, @PathVariable String issueId) throws IssueNotFoundException {
        Optional < Issue> issue = issueRepository.findById(issueId);
        if (!issue.isPresent()) {
            throw new IssueNotFoundException();
        }
        Comment newComment = new Comment();
        newComment.setBody(comment.getBody());
        issue.get().getComments().add(newComment);
        issueRepository.save(issue.get());
        return newComment;
    }

    // DELETE http://localhost:8080/gitminer/comments/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable String id) throws CommentNotFoundException {
        if(commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }else{
            throw new CommentNotFoundException();
        }
    }

    // PU http://localhost:8080/gitminer/comments/{id}
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/comments/{id}")
    public void updateComment(@RequestBody @Valid Comment comment, @PathVariable String id) throws CommentNotFoundException {
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
