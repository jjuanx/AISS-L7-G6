
package aiss.bitbucketminer.model.gitminer;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Project")
@JsonPropertyOrder({ "id", "name", "web_url", "commits", "issues" })
public class Project {

    @Id
    @JsonProperty("id")
    public String id;

    @JsonProperty("name")
    @NotEmpty(message = "The name of the project cannot be empty")
    public String name;

    @JsonProperty("web_url")
    @NotEmpty(message = "The URL of the project cannot be empty")
    public String webUrl;
    @JsonProperty("commits")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    private List<Commit> GMCommits;

    @JsonProperty("issues")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    private List<Issue> GMIssues;

    public Project() {
        GMCommits = new ArrayList<>();
        GMIssues = new ArrayList<>();
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

    public List<Commit> getCommits() {
        return GMCommits;
    }

    public void setCommits(List<Commit> GMCommits) {
        this.GMCommits = GMCommits;
    }

    public List<Issue> getIssues() {
        return GMIssues;
    }

    public void setIssues(List<Issue> GMIssues) {
        this.GMIssues = GMIssues;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("commits");
        sb.append('=');
        sb.append(((this.GMCommits == null)?"<null>":this.GMCommits));
        sb.append(',');
        sb.append("issues");
        sb.append('=');
        sb.append(((this.GMIssues == null)?"<null>":this.GMIssues));
        sb.append(',');

        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
