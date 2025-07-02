package aiss.gitminer.gitminer.controller;

import aiss.gitminer.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.DELETE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/gitminer/users";
    }

    @Test
    @DisplayName("GET /users retrieves all users")
    void getAllUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity(getBaseUrl(), User[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    @DisplayName("GET /users/{id} retrieves a user by ID")
    void getUserById() {
        User newUser = new User(UUID.randomUUID().toString(), "testUser", "Test User", "http://avatar.url", "http://web.url");
        ResponseEntity<User> postResponse = restTemplate.postForEntity(getBaseUrl(), newUser, User.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        ResponseEntity<User> response = restTemplate.getForEntity(getBaseUrl() + "/" + newUser.getId(), User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newUser.getId(), response.getBody().getId());
        assertEquals(newUser.getUsername(), response.getBody().getUsername());
        assertEquals(newUser.getName(), response.getBody().getName());
    }

    @Test
    @DisplayName("GET /users/{id} returns null for invalid ID")
    void getUserByIdThrowsException() {
        ResponseEntity<User> response = restTemplate.getForEntity(getBaseUrl() + "/invalid-id", User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("POST /users creates a new user")
    void createUser() {
        User newUser = new User(UUID.randomUUID().toString(), "newUser", "New User", "http://avatar.url", "http://web.url");
        ResponseEntity<User> response = restTemplate.postForEntity(getBaseUrl(), newUser, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newUser.getId(), response.getBody().getId());
        assertEquals(newUser.getUsername(), response.getBody().getUsername());
        assertEquals(newUser.getName(), response.getBody().getName());
    }

    @Test
    @DisplayName("DELETE /users/{id} deletes a user")
    void deleteUser() {
        User newUser = new User(UUID.randomUUID().toString(), "deleteUser", "Delete User", "http://avatar.url", "http://web.url");
        ResponseEntity<User> postResponse = restTemplate.postForEntity(getBaseUrl(), newUser, User.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        ResponseEntity<Void> response = restTemplate.exchange(getBaseUrl() + "/" + newUser.getId(), DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /users/{id} returns NOT_FOUND for invalid ID")
    void deleteUserThrowsException() {
        ResponseEntity<Void> response = restTemplate.exchange(getBaseUrl() + "/invalid-id", DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}