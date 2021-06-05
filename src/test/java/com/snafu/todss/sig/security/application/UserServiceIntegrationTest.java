package com.snafu.todss.sig.security.application;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.sun.jdi.request.DuplicateRequestException;
import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceIntegrationTest {
    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private SpringPersonRepository personRepository;


    @Autowired
    private UserService userService;

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
        dtoSupervisor.employedSince = "2005-12-01";
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
    @DisplayName("Creating a user")
    void createUser() {
        userService.register("TestUser1", "TestPassword", supervisor);

        assertEquals(2, userRepository.findAll().size());
    }

    @Test
    @DisplayName("Creating a duplicate user throws")
    void createDuplicateUser_Throws() {
        assertThrows(DuplicateRequestException.class,
                () -> userService.register("TestUser", "TestPassword", supervisor));
    }

    @Test
    @DisplayName("Get user by username returns user")
    void getUserByUsername_ReturnsUser() {
        User user1 = userService.loadUserByUsername(user.getUsername());

        assertEquals(user, user1);
    }

    @Test
    @DisplayName("Get user by unknown username returns user")
    void getUserByUnknownUsername_ReturnsUser() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("RandomUsername"));
    }

    @Test
    @DisplayName("Changing user role changes the role")
    void changeUserRole() {
        userService.setUserRole(user.getUsername(), "ROLE_MANAGER");

        assertEquals("ROLE_MANAGER", user.getRole().toString());
    }

    @Test
    @DisplayName("Changing user role of unknown user throws")
    void changeUserRoleOfUnknownUser_Throws() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.setUserRole("RandomUsername", "ROLE_MANAGER"));


    }

    @Test
    @DisplayName("Get all users returns list")
    void getAllUsers() {
        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

    @Test
    @DisplayName("Deleting user by username")
    void deleteUser() throws NotFoundException {
        userService.removeUser(user.getUsername());

        assertEquals(Collections.emptyList(), userRepository.findAll());
    }

    @Test
    @DisplayName("Deleting unknown user by username throws")
    void deleteUnknownUser_Throws() {
        assertThrows(
                NotFoundException.class,
                () -> userService.removeUser("RandomUsername"));
    }


}