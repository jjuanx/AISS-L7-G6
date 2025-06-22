package aiss.githubminer.tranformer;

import aiss.githubminer.model.*;
import aiss.githubminer.model.gitminer.GMComment;
import aiss.githubminer.model.gitminer.GMCommit;
import aiss.githubminer.model.gitminer.GMIssue;
import aiss.githubminer.model.gitminer.GMUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommitMapper {
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


}
