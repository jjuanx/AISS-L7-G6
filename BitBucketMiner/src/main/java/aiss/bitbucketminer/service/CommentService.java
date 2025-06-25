package aiss.bitbucketminer.service;

import aiss.bitbucketminer.etl.Transformer;
import aiss.bitbucketminer.model.bitbucket.BitBucketComment;
import aiss.bitbucketminer.model.gitminer.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @Autowired
    private RestTemplate restTemplate;


    public List<Comment> fetchCommentsForIssue(String workspace, String repoSlug, Integer issueId) {
        String url = String.format(
                "https://api.bitbucket.org/2.0/repositories/%s/%s/issues/%d/comments",
                workspace, repoSlug, issueId
        );

        List<Comment> comments = new ArrayList<>();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawComments = (List<Map<String, Object>>) response.getBody().get("values");

        if (rawComments != null) {
            for (Map<String, Object> raw : rawComments) {
                Integer commentId = (Integer) raw.get("id");

                BitBucketComment bitbucketComment = restTemplate.getForObject(
                        url + "/" + commentId,
                        BitBucketComment.class
                );

                Comment comment = Transformer.toGitMinerComment(bitbucketComment);
                comments.add(comment);
            }
        }

        return comments;
    }
}
