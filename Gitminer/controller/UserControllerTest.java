package aiss.gitminer.controller;

import aiss.gitminer.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/gitminer/users";

    @Test
    @DisplayName("GET /users returns list of users")
    void getAllUsers() {
        ResponseEntity<List<User>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /users/{id} returns user if exists")
    void getUserById() {
        User newUser = new User("testId", "testuser", "Test User", "http://avatar.com/test", "http://web.com/test");
        restTemplate.postForEntity(baseUrl, newUser, User.class);

        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/testId", User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    @DisplayName("POST /users creates a user")
    void createUser() {
        User newUser = new User("createTest", "newuser", "New User", "http://avatar.com/new", "http://web.com/new");

        ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, newUser, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getUsername());
    }

    @Test
    @DisplayName("DELETE /users/{id} deletes user if exists")
    void deleteUser() {
        User userToDelete = new User("deleteTest", "todelete", "To Delete", "http://avatar.com/delete", "http://web.com/delete");
        restTemplate.postForEntity(baseUrl, userToDelete, User.class);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/deleteTest",
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
