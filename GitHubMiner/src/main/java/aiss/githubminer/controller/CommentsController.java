package aiss.githubminer.controller;

import aiss.githubminer.service.CommentsService;
import aiss.githubminer.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/github")
public class CommentsController {

    @Autowired
    CommentsService commentsService;

    @GetMapping("/{owner}/{repo}/issues/comments")
    public List<Comment> getComments(@PathVariable String owner,
                                     @PathVariable String repo,
                                     @RequestParam(required = false, defaultValue = "2") Integer maxPages
    ) {
        return commentsService.getComments(owner, repo, maxPages);
    }
}
