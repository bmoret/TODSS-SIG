package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    private PersonService personService;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private SpringPersonRepository personRepository;

    @BeforeEach
    void setUp() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test2@email.com";
        dtoSupervisor.firstname = "Test";
        dtoSupervisor.lastname = "Person";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "01/01/2021";
        dtoSupervisor.supervisorId = null;
        Person supervisor = personService.createPerson(dtoSupervisor);
        personRepository.save(supervisor);

        User user = new User("TestUser", "TestPassword", supervisor);
        userRepository.save(user);

    }

    @AfterEach
    void tearDown() {
        this.userRepository.deleteAll();
        this.personRepository.deleteAll();
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
        jsonObject.put("employedSince", "01/01/2021");
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
        jsonObject.put("email", "test2@email.com");
        jsonObject.put("firstname", "FirstName");
        jsonObject.put("lastname", "LastName");
        jsonObject.put("expertise", "none");
        jsonObject.put("branch", "VIANEN");
        jsonObject.put("role", "EMPLOYEE");
        jsonObject.put("employedSince", "01/01/2021");
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
        jsonObject.put("email", "test2@email.com");
        jsonObject.put("firstname", "FirstName");
        jsonObject.put("lastname", "LastName");
        jsonObject.put("expertise", "none");
        jsonObject.put("branch", "VIANEN");
        jsonObject.put("role", "EMPLOYEE");
        jsonObject.put("employedSince", "01/01/2021");
        jsonObject.put("supervisorId", null);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/registration")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

}