package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.UserProfile;
import com.snafu.todss.sig.security.presentation.dto.request.Login;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationIntegrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringUserRepository userRepository;

    @AfterEach
    void tearDown() {
        this.userRepository.deleteAll();
    }

    @Test
    @DisplayName("Creating a user with password length shorter than 7 throws exception")
    void createUserWithPasswordLengthShorterThan7_ThrowsException() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "TestUserName");
        jsonObject.put("password", "123456");
        jsonObject.put("email", "test2@email.com");
        jsonObject.put("firstname", "FirstName");
        jsonObject.put("lastname", "LastName");
        jsonObject.put("expertise", "none");
        jsonObject.put("branch", "VIANEN");
        jsonObject.put("role", "EMPLOYEE");
        jsonObject.put("employedSince", "2005/12/01");
        jsonObject.put("supervisorId", null);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/registration")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Creating a user with password length of 7 does not throw exception")
    void createUser_DoesNotThrow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "TestUserName");
        jsonObject.put("password", "1234567");
        jsonObject.put("email", "test3@email.com");
        jsonObject.put("firstname", "FirstName");
        jsonObject.put("lastname", "LastName");
        jsonObject.put("expertise", "none");
        jsonObject.put("branch", "VIANEN");
        jsonObject.put("role", "EMPLOYEE");
        jsonObject.put("employedSince", "2005/12/01");
        jsonObject.put("supervisorId", null);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/registration")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Creating a user with password length over 7 does not throw exception")
    void createUserWithPasswordLengthOver7_DoesNotThrow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "TestUserName");
        jsonObject.put("password", "12345678");
        jsonObject.put("email", "test4@email.com");
        jsonObject.put("firstname", "FirstName");
        jsonObject.put("lastname", "LastName");
        jsonObject.put("expertise", "none");
        jsonObject.put("branch", "VIANEN");
        jsonObject.put("role", "EMPLOYEE");
        jsonObject.put("employedSince", "2005/12/01");
        jsonObject.put("supervisorId", null);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/registration")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Creating a new usr profile")
    void createUserProfile() {
        UserProfile userProfile = new UserProfile("TestUser");

        assertEquals("TestUser", userProfile.getUsername());
    }

    @Test
    @DisplayName("Creating a login DTO")
    void createLoginDTO(){
        Login login = new Login();
        login.username = "TestUser";
        login.password = "TestPassword";
        
        assertEquals("TestUser", login.username);
        assertEquals("TestPassword", login.password);
    }

}