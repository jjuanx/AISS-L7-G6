package aiss.gitminer.controller;

import aiss.gitminer.model.Comment;
import aiss.gitminer.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public List<Comment> getAllComments (){
        return commentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElse(null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Comment createComment(@RequestBody @Valid Comment comment) {
        return commentRepository.save(comment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable String id) {
        if(commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
    }
}
