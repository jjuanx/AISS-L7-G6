package aiss.githubminer.service;

import aiss.githubminer.model.DataModel.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Get the user from a url")
    void getUser() {
        String url = "https://api.github.com/users/quaff";
        UserData user = userService.getUser(url);
        assertNotNull(user);
        System.out.println(user);
    }
}