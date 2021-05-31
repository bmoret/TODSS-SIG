package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private SpringPersonRepository personRepository;

    private User user;

    private Person supervisor;

    @BeforeEach
    void setUp() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test2@email.com";
        dtoSupervisor.firstname = "fourth";
        dtoSupervisor.lastname = "last";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2005/12/01";
        dtoSupervisor.supervisorId = null;
        supervisor = personService.createPerson(dtoSupervisor);
        personRepository.save(supervisor);

        user = new User("TestUser", "TestPassword", supervisor);
        this.userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        this.userRepository.deleteAll();
        this.personRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get all users as manager returns list of users")
    void getAllUsersAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Get all users as administrator returns list of users")
    void getAllUsersAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "Employee")
    @DisplayName("Get all users as employee is not allowed")
    void getAllUsersAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get user by username as manager returns user")
    void getUserByUserNameAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/user/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Get user by username as administrator returns user")
    void getUserByUserNameAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/user/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Get user by username as employee is not allowed")
    void getUserByUserNameAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/user/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Delete user as manager returns no content")
    void deleteUserAsManager_ReturnsNoContent() throws Exception {
        User user = new User("TestUsername1", "TestPassword", supervisor);
        userRepository.save(user);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/user/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Delete user as administrator returns no content")
    void deleteUserAsAdministrator_ReturnsNoContent() throws Exception {
        User user = new User("TestUsername1", "TestPassword", supervisor);
        userRepository.save(user);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/user/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Delete user as employee returns no content")
    void deleteUserAsEmployee_ReturnsNoContent() throws Exception {
        User user = new User("TestUsername1", "TestPassword", supervisor);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/user/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

}