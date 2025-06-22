package aiss.githubminer.tranformer;

import aiss.githubminer.model.*;
import aiss.githubminer.model.gitminer.GMComment;
import aiss.githubminer.model.gitminer.GMCommit;
import aiss.githubminer.model.gitminer.GMIssue;
import aiss.githubminer.model.gitminer.GMUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IssueMapper {

    public static GMIssue mapIssue(Issue ghIssue, List<GMComment> comments) {
        GMIssue i = new GMIssue();
        i.setId(String.valueOf(ghIssue.getId()));
        i.setRefId(String.valueOf(ghIssue.getNumber()));
        i.setTitle(ghIssue.getTitle());
        i.setDescription(ghIssue.getBody());
        i.setState(ghIssue.getState());
        i.setCreatedAt(ghIssue.getCreatedAt());
        i.setUpdatedAt(ghIssue.getUpdatedAt());
        i.setClosedAt(String.valueOf(ghIssue.getClosedAt()));
        List<String> labelNames = ghIssue.getLabels().stream()
                .map(label -> label.getName()) // o label.getLabel() si se llama así
                .toList();
        i.setLabels(labelNames);
        i.setAuthor(UserMapper.mapUser(ghIssue.getUser()));
        i.setAssignee(UserMapper.mapUser(ghIssue.getAssignee()));
        i.setUpvotes(0);
        i.setDownvotes(0);
        i.setWebUrl(ghIssue.getHtmlUrl());
        i.setComments(comments);
        return i;
    }

    public static List<GMIssue> mapIssues(List<Issue> issues, List<Comment> allComments) {
        List<GMIssue> res = new ArrayList<>();

        for (Issue issue : issues) {
            List<GMComment> relatedComments = allComments.stream()
                    .filter(c -> {
                        String url = c.getIssueUrl();
                        return url != null && url.contains("/issues/" + issue.getNumber());
                    })
                    .map(IssueMapper::mapComment)
                    .collect(Collectors.toList());

            res.add(mapIssue(issue, relatedComments));
        }

        return res;
    }

    public static GMComment mapComment(Comment comment) {
        GMComment gmComment = new GMComment();
        gmComment.setId(String.valueOf(comment.getId()));
        gmComment.setBody(comment.getBody());
        gmComment.setAuthor(UserMapper.mapUser(comment.getUser()));
        gmComment.setCreatedAt(comment.getCreatedAt());
        gmComment.setUpdatedAt(comment.getUpdatedAt());
        return gmComment;
    }

    public static List<GMComment> mapComments(List<Comment> comments) {
        List<GMComment> res = new ArrayList<>();
        for (Comment c : comments) {
            res.add(mapComment(c));
        }
        return res;
    }

}
