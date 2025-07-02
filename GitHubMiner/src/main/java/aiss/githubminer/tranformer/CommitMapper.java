package aiss.githubminer.tranformer;

import aiss.githubminer.model.DataModel.CommitData.CommitDatum;
import aiss.githubminer.model.GitMinerModel.CommitGitMiner;
import aiss.githubminer.service.CommitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommitMapper {

    @Autowired
    private CommitsService commitService;

    public List<CommitGitMiner> getCommits(String owner, String repo, Integer sinceCommits, Integer maxPages) {
        List<CommitGitMiner> commits = new ArrayList<>();
        List<CommitDatum> datas = commitService.getCommits(owner, repo, sinceCommits, maxPages);
        for (CommitDatum data : datas) {
            CommitGitMiner commit = new CommitGitMiner();
            commit.setId(data.getSha());
            
            String message = data.getCommit().getMessage();
            String title = message != null && !message.isEmpty() ? 
                message.split("\n")[0] : "";
            
            commit.setTitle(title);
            commit.setMessage(message);
            commit.setAuthorName(data.getCommit().getAuthor().getName());
            commit.setAuthorEmail(data.getCommit().getAuthor().getEmail());
            commit.setAuthoredDate(data.getCommit().getAuthor().getDate());
            commit.setWebUrl(data.getHtmlUrl());
            commits.add(commit);
        }
        return commits;
    }
}
