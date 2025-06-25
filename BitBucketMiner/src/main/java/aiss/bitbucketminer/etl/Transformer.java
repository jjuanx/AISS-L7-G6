package aiss.bitbucketminer.etl;

import aiss.bitbucketminer.model.bitbucket.*;
import aiss.bitbucketminer.model.gitminer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transformer {

    public static Commit toGitMinerCommit(BitbucketCommit bitbucketCommit) {
        Commit commit = new Commit();

        commit.setId(bitbucketCommit.getHash());

        String[] messageLines = bitbucketCommit.getMessage().split("\n", 2);
        commit.setTitle(messageLines[0]);
        commit.setMessage(bitbucketCommit.getMessage());

        String rawAuthor = bitbucketCommit.getAuthor() != null ? bitbucketCommit.getAuthor().getRaw() : "Unknown";
        String[] parts = rawAuthor.split("<");
        String authorName = parts[0].trim();
        String authorEmail = (parts.length > 1) ? parts[1].replace(">", "").trim() : "";

        commit.setAuthorName(authorName);
        commit.setAuthorEmail(authorEmail);
        commit.setCommitterName(authorName);
        commit.setCommitterEmail(authorEmail);
        commit.setAuthoredDate(bitbucketCommit.getDate());
        commit.setCommittedDate(bitbucketCommit.getDate());

        if (bitbucketCommit.getLinks() != null && bitbucketCommit.getLinks().get("html") != null) {
            commit.setWebUrl(bitbucketCommit.getLinks().get("html").getHref());
        }

        return commit;
    }

    public static Issue toGitMinerIssue(BitBucketIssue bitbucketIssue) {
        Issue issue = new Issue();

        issue.setId(String.valueOf(bitbucketIssue.getId()));
        issue.setRefId(String.valueOf(bitbucketIssue.getId()));
        issue.setTitle(bitbucketIssue.getTitle());

        String description = (bitbucketIssue.getContent() != null)
                ? bitbucketIssue.getContent().getRaw()
                : "";
        issue.setDescription(description);

        issue.setState(bitbucketIssue.getState());
        issue.setCreatedAt(bitbucketIssue.getCreated_on());
        issue.setUpdatedAt(bitbucketIssue.getUpdated_on());
        issue.setClosedAt(null);

        List<String> labels = new ArrayList<>();
        if (bitbucketIssue.getKind() != null) labels.add(bitbucketIssue.getKind());
        if (bitbucketIssue.getPriority() != null) labels.add(bitbucketIssue.getPriority());
        issue.setLabels(labels);

        User author = new User();
        if (bitbucketIssue.getReporter() != null) {
            String username = bitbucketIssue.getReporter().getDisplay_name();
            author.setName(username);
            author.setUsername(username);
            String userId = bitbucketIssue.getReporter().getDisplay_name();
            if (userId == null || userId.isBlank()) {
                userId = UUID.randomUUID().toString();
            }
            author.setId(userId);
            author.setAvatarUrl(null);
            author.setWebUrl(null);
        } else {
            author.setName("Unknown");
            author.setUsername("unknown");
            author.setId("unknown");
            author.setAvatarUrl(null);
            author.setWebUrl(null);
        }
        issue.setAuthor(author);
        issue.setAssignee(null);
        issue.setUpvotes(0);
        issue.setDownvotes(0);

        if (bitbucketIssue.getLinks() != null && bitbucketIssue.getLinks().getHtml() != null) {
            issue.setWebUrl(bitbucketIssue.getLinks().getHtml().getHref());
        }

        issue.setComments(new ArrayList<>());
        return issue;
    }

    public static Comment toGitMinerComment(BitBucketComment bitbucketComment) {
        Comment comment = new Comment();

        comment.setId(bitbucketComment.getId() != null ? String.valueOf(bitbucketComment.getId()) : UUID.randomUUID().toString());

        String body = (bitbucketComment.getContent() != null && bitbucketComment.getContent().getRaw() != null && !bitbucketComment.getContent().getRaw().isBlank())
                ? bitbucketComment.getContent().getRaw()
                : "No content";

        comment.setBody(body);

        comment.setCreatedAt(bitbucketComment.getCreated_on() != null ? bitbucketComment.getCreated_on() : "");
        comment.setUpdatedAt(bitbucketComment.getUpdated_on());

        User author = new User();
        if (bitbucketComment.getUser() != null) {
            author.setName(bitbucketComment.getUser().getDisplay_name());
            author.setUsername(bitbucketComment.getUser().getDisplay_name());

            String userId = bitbucketComment.getUser().getUuid();
            if (userId == null || userId.isBlank()) userId = UUID.randomUUID().toString();
            author.setId(userId);

            author.setAvatarUrl(null);
            author.setWebUrl(null);
        } else {
            author.setName("Unknown");
            author.setUsername("unknown");
            author.setId(UUID.randomUUID().toString());
            author.setAvatarUrl(null);
            author.setWebUrl(null);
        }
        comment.setAuthor(author);

        return comment;
    }


    public static Project toGitMinerProject(BitBucketProject bitbucketProject) {
        Project project = new Project();

        String cleanId = bitbucketProject.getUuid().replace("{", "").replace("}", "");
        project.setId(cleanId);
        project.setName(bitbucketProject.getName());

        if (bitbucketProject.getLinks() != null && bitbucketProject.getLinks().getHtml() != null) {
            project.setWebUrl(bitbucketProject.getLinks().getHtml().getHref());
        }

        project.setCommits(new ArrayList<>());
        project.setIssues(new ArrayList<>());

        return project;
    }
}
