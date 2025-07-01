package aiss.githubminer.tranformer;

import aiss.githubminer.model.DataModel.IssueData.IssueDatum;
import aiss.githubminer.model.DataModel.UserData;
import aiss.githubminer.model.GitMinerModel.IssueGitMiner;
import aiss.githubminer.model.GitMinerModel.UserGitMiner;
import aiss.githubminer.service.IssuesService;
import aiss.githubminer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class IssueMapper {

    @Autowired
    private IssuesService issueService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentMapper commentMapper;

    public List<IssueGitMiner> getIssues(String owner, String repo, Integer sinceIssues, Integer maxPages) {
        List<IssueGitMiner> issues = new ArrayList<>();
        List<IssueDatum> datas = issueService.getIssues(owner, repo, sinceIssues, maxPages);
        for (IssueDatum data : datas) {
            IssueGitMiner issue = new IssueGitMiner();
            issue.setId(data.getId().toString());
            issue.setTitle(data.getTitle());
            issue.setDescription(data.getBody());
            issue.setState(data.getState());
            issue.setCreatedAt(data.getCreatedAt());
            issue.setUpdatedAt(data.getUpdatedAt());
            issue.setClosedAt(data.getClosedAt());
            issue.setLabels(data.getLabels().stream().map(l -> l.getName()).collect(Collectors.toList()));
            issue.setVotes(data.getReactions().getTotalCount());
            String user_url = data.getUser().getUrl();
            if(user_url.contains("%5B")){
                user_url = user_url.replace("%5B","[").replace("%5D","]");
            }
            issue.setAuthor(getUser(user_url));
            if(data.getAssignee() != null) {
                issue.setAssignee(getUser(data.getAssignee().getUrl()));
            }
            issue.setComments(commentMapper.getComments(data.getCommentsUrl()));
            issues.add(issue);
        }
        return issues;
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
