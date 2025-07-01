package aiss.githubminer.model.GitMinerModel;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ProjectGitMiner {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("commits")
    private List<CommitGitMiner> commits;
    @JsonProperty("issues")
    private List<IssueGitMiner> issues;

    public ProjectGitMiner() {
        commits = new ArrayList<>();
        issues = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public List<CommitGitMiner> getCommits() {
        return commits;
    }

    public void setCommits(List<CommitGitMiner> commitGitMiners) {
        this.commits = commitGitMiners;
    }

    public List<IssueGitMiner> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueGitMiner> issueGitMiners) {
        this.issues = issueGitMiners;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ProjectGitMiner.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("commits");
        sb.append('=');
        sb.append(((this.commits == null)?"<null>":this.commits));
        sb.append(',');
        sb.append("issues");
        sb.append('=');
        sb.append(((this.issues == null)?"<null>":this.issues));
        sb.append(',');

        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}