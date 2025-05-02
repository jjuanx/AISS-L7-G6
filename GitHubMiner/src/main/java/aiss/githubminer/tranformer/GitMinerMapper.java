package aiss.githubminer.tranformer;

import aiss.githubminer.model.*;
import aiss.githubminer.model.gitminer.GMComment;
import aiss.githubminer.model.gitminer.GMCommit;
import aiss.githubminer.model.gitminer.GMIssue;
import aiss.githubminer.model.gitminer.GMUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GitMinerMapper {
    public static GMCommit mapCommit(Commit commit, String projectWebUrl) {
        Commit__1 info = commit.getCommit();

        GMCommit gmCommit = new GMCommit();
        gmCommit.setId(commit.getSha());
        gmCommit.setTitle(extractTitle(info.getMessage()));
        gmCommit.setMessage(info.getMessage());

        gmCommit.setAuthorName(info.getAuthor().getName());
        gmCommit.setAuthorEmail(info.getAuthor().getEmail());
        gmCommit.setAuthoredDate(info.getAuthor().getDate());

        gmCommit.setCommitterName(info.getCommitter().getName());
        gmCommit.setCommitterEmail(info.getCommitter().getEmail());
        gmCommit.setCommittedDate(info.getCommitter().getDate());

        gmCommit.setWebUrl(commit.getHtmlUrl() != null ? commit.getHtmlUrl() : projectWebUrl);
        return gmCommit;
    }

    public static List<GMCommit> mapCommits(List<Commit> commits, String projectWebUrl) {
        List<GMCommit> result = new ArrayList<>();
        for (Commit c : commits) {
            result.add(mapCommit(c, projectWebUrl));
        }
        return result;
    }

    private static String extractTitle(String message) {
        if (message == null) return "";
        int index = message.indexOf('\n');
        return index == -1 ? message : message.substring(0, index);
    }

    public static GMUser mapUser(User user) {
        if(user == null) return null;
        GMUser u = new GMUser();
        u.setId(String.valueOf(user.getId()));
        u.setUsername(user.getLogin());
        u.setName(user.getLogin());
        u.setAvatarUrl(user.getAvatarUrl());
        u.setWebUrl(user.getHtmlUrl());
        return u;
    }

    public static List<GMUser> mapUsers(List<User> users, String projectWebUrl) {
        List<GMUser> result = new ArrayList<>();
        for (User user : users) {
            result.add(mapUser(user));
        }
        return result;
    }

    public static GMComment mapComment(Comment comment) {
        GMComment gmComment = new GMComment();
        gmComment.setId(String.valueOf(comment.getId()));
        gmComment.setBody(comment.getBody());
        gmComment.setAuthor(mapUser(comment.getUser()));
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
        i.setAuthor(mapUser(ghIssue.getUser()));
        i.setAssignee(mapUser(ghIssue.getAssignee()));
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
                    .map(GitMinerMapper::mapComment)
                    .collect(Collectors.toList());

            res.add(mapIssue(issue, relatedComments));
        }

        return res;
    }

}
