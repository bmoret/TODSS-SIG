package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.CiTestConfiguration;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.*;
import com.snafu.todss.sig.sessies.domain.person.enums.*;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(CiTestConfiguration.class)
@Transactional
class PersonServiceTest {

    @Autowired
    private PersonService service;

    @BeforeEach
    void beforeEach() throws NotFoundException {
        service.getPersonByEmail("email2@email.com").setSupervisor(service.getPersonByEmail("email@email.com"));
    }

    @Test
    void getPersonByEmail() {
        try {
            Person person = service.getPersonByEmail("email2@email.com");
            assertEquals("email2@email.com",person.getDetails().getEmail());
            assertEquals("second",person.getDetails().getFirstname());
            assertEquals("last",person.getDetails().getLastname());
            assertEquals("none",person.getDetails().getExpertise());
            assertEquals(LocalDate.of(2000,1,1),person.getDetails().getEmployedSince());
            assertEquals(Branch.VIANEN,person.getDetails().getBranch());
            assertEquals(Role.EMPLOYEE,person.getDetails().getRole());
            assertEquals("first",person.getSupervisor().getDetails().getFirstname());
        } catch (NotFoundException e) {
            fail();
        }
    }

    @Test
    void createPerson() {
        Person supervisor = null;
        try {
            supervisor = service.getPersonByEmail("email@email.com");
        } catch (NotFoundException e) {
            fail();
        }
        PersonRequest dto = new PersonRequest();
        dto.email = "email4@email.com";
        dto.firstname = "fourth";
        dto.lastname = "last";
        dto.expertise = "none";
        dto.branch = "VIANEN";
        dto.role = "EMPLOYEE";
        dto.employedSince = "01/01/2021";
        dto.supervisorId = supervisor.getId();

        Person person = null;
        try {
            person = service.createPerson(dto);
        } catch (NotFoundException e) {
            fail();
        }
        assertEquals("email4@email.com", person.getDetails().getEmail());

        try {
            assertEquals("fourth", service.getPersonByEmail("email4@email.com").getDetails().getFirstname());
        } catch (NotFoundException e) {
            fail();
        }

    }

    @Test
    void editPerson() {
        Person supervisor = null;
        try {
            supervisor = service.getPersonByEmail("email@email.com");
        } catch (NotFoundException e) {
            fail();
        }
        PersonRequest dto = new PersonRequest();
        dto.email = "email2@email.com";
        dto.firstname = "second";
        dto.lastname = "last";
        dto.expertise = "all";
        dto.branch = "VIANEN";
        dto.role = "EMPLOYEE";
        dto.employedSince = "01/01/2000";
        dto.supervisorId = supervisor.getId();

        Person person = null;
        try {
            person = service.editPerson(
                    service.getPersonByEmail("email2@email.com").getId()
                    ,dto);
        } catch (NotFoundException e) {
            fail();
        }
        assertEquals("email2@email.com", person.getDetails().getEmail());
        assertEquals("all", person.getDetails().getExpertise());

        try {
            assertEquals("second", service.getPersonByEmail("email2@email.com").getDetails().getFirstname());
            assertEquals("all", service.getPersonByEmail("email2@email.com").getDetails().getExpertise());
        } catch (NotFoundException e) {
            fail();
        }
    }

    @Test
    void removePerson() {
        Person person = null;
        try {
            person = service.getPersonByEmail("email3@email.com");
            service.removePerson(person.getId());
        } catch (NotFoundException e) {
            fail();
        }
        assertThrows(
                NotFoundException.class
                , () ->service.getPersonByEmail("email3@email.com")
        );
    }
}