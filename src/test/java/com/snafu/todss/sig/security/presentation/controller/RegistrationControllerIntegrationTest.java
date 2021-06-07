package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.domain.UserProfile;
import com.snafu.todss.sig.security.presentation.dto.request.Login;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private SpringPersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        jsonObject.put("employedSince", "2005-12-01");
        jsonObject.put("supervisorId", null);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/registration")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request);
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
        jsonObject.put("employedSince", "2005-12-01");
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
        jsonObject.put("employedSince", "2005-12-01");
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
    void createLoginDTO() {
        Login login = new Login();
        login.username = "TestUser";
        login.password = "TestPassword";

        assertEquals("TestUser", login.username);
        assertEquals("TestPassword", login.password);
    }

    @Test
    @DisplayName("log in")
    MvcResult loggingIn() throws Exception {
        createDefaultTestUser();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "TestUser");
        jsonObject.put("password", "TestPassword");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/login")
                .content(jsonObject.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        return mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();
    }

    private User createDefaultTestUser() {
        Person person = personRepository.save(new PersonBuilder().setRole(Role.MANAGER).build());
        User user = new User("TestUser", passwordEncoder.encode("TestPassword"), person);
        return userRepository.save(user);
    }

    @Test
    @DisplayName("invalid login credentials throw 401")
    void invalidLogin() throws Exception {
        createDefaultTestUser();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "wrong");
        jsonObject.put("password", "wrong");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/login")
                .content(jsonObject.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Refresh access token with refresh token")
    void refreshAccessToken() throws Exception {
        Map<String, String> tokens = getTokensFromLogin();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", tokens.get("access_token").split(" ")[1]);
        jsonObject.put("refreshToken", tokens.get("refresh_token"));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/authenticate/refresh")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    private Map<String, String> getTokensFromLogin() throws Exception {
        MvcResult result = loggingIn();
        return Map.of(
                "access_token", result.getResponse().getHeader("Access-Token"),
                "refresh_token", result.getResponse().getHeader("Refresh-Token")
        );
    }

    @Test
    @DisplayName("wrong access token throws")
    void wrongAccessTokenThrows() throws Exception {
        Map<String, String> tokens = getTokensFromLogin();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", "randomtokenddd.sdasdad.asdasd");
        jsonObject.put("refreshToken", tokens.get("refresh_token"));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/authenticate/refresh")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("wrong refresh token throws")
    void wrongRefreshTokenThrows() throws Exception {
        Map<String, String> tokens = getTokensFromLogin();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", tokens.get("access_token").split(" ")[1]);
        jsonObject.put("refreshToken", "randomtokenddd.sdasdad.asdasd");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/authenticate/refresh")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("request without token")
    void requestWithoutToken() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/random")
                .contentType("application/json");

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("tokens")
    @DisplayName("request with empty token")
    void requestWithEmptyToken(String token) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/random")
                .contentType("application/json")
                .header("Access-Token", token);

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    private static Stream<Arguments> tokens() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("bbbb")
        );
    }

    @Test
    @DisplayName("Refresh access token with refresh token")
    void requestWithCorrectTokens() throws Exception {
        Map<String, String> tokens = getTokensFromLogin();

        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions")
                .contentType("application/json")
                .header("Access-Token", tokens.get("access_token"));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}