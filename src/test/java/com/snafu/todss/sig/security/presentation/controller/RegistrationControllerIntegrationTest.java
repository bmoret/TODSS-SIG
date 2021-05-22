package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import javassist.NotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationIntegrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringUserRepository userRepository;

    @AfterEach
    void tearDown() throws NotFoundException {
        this.userRepository.deleteAll();
    }

    @Test
    @DisplayName("Creating a user with password length shorter than 7 throws exception")
    void createUserWithPasswordLengthShorterThan7_ThrowsException() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "TestUserName");
        jsonObject.put("password", "short");

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

        RequestBuilder request = MockMvcRequestBuilders
                .post("/registration")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Creating a user with password length over 7 does not throw exception")
    void createUserWithPasswordLengthOver7_DoesNotThrow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "TestUserName2");
        jsonObject.put("password", "testPassWord");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/registration")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }



}