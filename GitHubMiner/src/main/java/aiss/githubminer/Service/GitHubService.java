package aiss.githubminer.Service;

import aiss.githubminer.model.Comment;
import aiss.githubminer.model.Commit;
import aiss.githubminer.model.Issue;
import aiss.githubminer.model.gitminer.GMProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class GitHubService {

    @Autowired
    RestTemplate restTemplate;


    private final String baseUri = "https://api.github.com/";

    //https://api.github.com/repos/OWNER/REPO
    public GMProject getProject(String owner, String repo) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUri + "repos/" + owner + "/" + repo);
        String uri = builder.toUriString();
        GMProject GMProject = restTemplate.getForObject(uri, GMProject.class);
        return GMProject;
    }

    public void createProject(String owner, String repo, GMProject GMProject) {}



    //https://api.github.com/repos/OWNER/REPO/commits
    public List<Commit> getCommits(String owner, String repo) {
        return getCommits(owner, repo, 2, 2);
    }

    public List<Commit> getCommits(String owner, String repo, Integer maxPages, Integer sinceCommits){
        int perPage = (maxPages != null) ? maxPages : 2;
        int sinceDays = (sinceCommits != null) ? sinceCommits : 2;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUri + "repos/" + owner + "/" + repo + "/commits")
                .queryParam("per_page", perPage);

        long millis = System.currentTimeMillis() - ((long) sinceDays * 24 * 60 * 60 * 1000);
        Date sinceDate = new Date(millis);
        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        iso.setTimeZone(TimeZone.getTimeZone("UTC"));
        builder.queryParam("since", iso.format(sinceDate));

        String uri = builder.toUriString();
        Commit[] commits = restTemplate.getForObject(uri, Commit[].class);
        return (commits != null) ? Arrays.asList(commits) : List.of();
    }



    //  https://api.github.com/repos/OWNER/REPO/issues
    public List<Issue> getIssues(String owner, String repo) {
        return getIssues(owner, repo, 2, 20);
    }

    public List<Issue> getIssues(String owner, String repo, Integer maxPages, Integer sinceIssues){
        int perPage = (maxPages != null) ? maxPages : 2;
        int sinceDays = (sinceIssues != null) ? sinceIssues : 20;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUri + "repos/" + owner + "/" + repo + "/issues")
                .queryParam("per_page", perPage);

        long millis = System.currentTimeMillis() - ((long) sinceDays * 24 * 60 * 60 * 1000);
        Date sinceDate = new Date(millis);
        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        iso.setTimeZone(TimeZone.getTimeZone("UTC"));
        builder.queryParam("since", iso.format(sinceDate));

        String uri = builder.toUriString();
        Issue[] issues = restTemplate.getForObject(uri, Issue[].class);
        if(issues == null) { return List.of();}
        List<Issue> res = Arrays.asList(issues).stream().toList();
        return res;
    }




    public List<Comment> getComments(String owner, String repo) {
        return getComments(owner, repo, 2);
    }
    //https://api.github.com/repos/OWNER/REPO/issues/comments
    public List<Comment> getComments(String owner, String repo, Integer maxPages){
        int perPage = (maxPages != null) ? maxPages : 2;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUri + "repos/" + owner + "/" + repo + "/issues/comments")
                .queryParam("per_page", perPage);

        String uri = builder.toUriString();
        Comment[] comments = restTemplate.getForObject(uri, Comment[].class);
        if(comments==null) { return List.of(); }
        List<Comment> res = Arrays.asList(comments).stream().toList();
        return res;
    }
}
