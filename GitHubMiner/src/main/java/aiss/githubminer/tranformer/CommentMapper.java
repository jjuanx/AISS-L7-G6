package aiss.githubminer.tranformer;


import aiss.githubminer.model.DataModel.CommentData.CommentDatum;
import aiss.githubminer.model.DataModel.UserData;
import aiss.githubminer.model.GitMinerModel.CommentGitMiner;
import aiss.githubminer.model.GitMinerModel.UserGitMiner;
import aiss.githubminer.service.CommentsService;
import aiss.githubminer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentMapper {

    @Autowired
    private CommentsService commentService;
    @Autowired
    private UserService userService;

    public List<CommentGitMiner> getComments(String url) {
        List<CommentGitMiner> comments = new ArrayList<>();
        List<CommentDatum> commentData = commentService.getComments(url);
        for (CommentDatum data : commentData) {
            CommentGitMiner comment = new CommentGitMiner();
            comment.setId(data.getId().toString());
            comment.setBody(data.getBody());
            comment.setCreatedAt(data.getCreatedAt());
            comment.setUpdatedAt(data.getUpdatedAt());
            comment.setAuthor(getUser(data.getUser().getUrl()));
            comments.add(comment);
        }
        return comments;
    }

    private UserGitMiner getUser(String url) {
        UserGitMiner user = new UserGitMiner();
        UserData data = userService.getUser(url);
        user.setId(data.getId().toString());
        user.setName(data.getName());
        user.setUsername(data.getLogin());
        user.setAvatarUrl(data.getAvatarUrl());
        user.setWebUrl(data.getHtmlUrl());
        return user;
    }


}
