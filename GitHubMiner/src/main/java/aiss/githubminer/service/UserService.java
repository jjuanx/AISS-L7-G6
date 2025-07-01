package aiss.githubminer.service;

import aiss.githubminer.model.DataModel.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class UserService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${github.token}")
    private String token;

    public UserData getUser(String url) {
        String uri = url;
        HttpHeaders headers = new HttpHeaders();
        if(!Objects.equals(token, "")) {
            headers.add("Authorization", "Bearer " + token);
        }
        HttpEntity<UserData> entity = new HttpEntity<>(null, headers);
        ResponseEntity<UserData> response = restTemplate.exchange(uri, HttpMethod.GET, entity, UserData.class);
        return response.getBody();
    }
}
