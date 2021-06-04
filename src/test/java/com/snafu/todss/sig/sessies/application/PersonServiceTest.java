package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.CiTestConfiguration;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.*;
import com.snafu.todss.sig.sessies.domain.person.enums.*;
import com.sun.jdi.request.DuplicateRequestException;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.transaction.Transactional;

import java.time.LocalDate;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(CiTestConfiguration.class)
@Transactional
class PersonServiceTest {

    @Autowired
    private PersonService service;
    @Autowired
    private SpringPersonRepository repo;

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
        dto.employedSince = "2021-01-01";
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
    void createPersonDuplicateEmail() {
        PersonRequest dto = new PersonRequest();
        dto.email = "email2@email.com";
        dto.firstname = "fourth";
        dto.lastname = "last";
        dto.expertise = "none";
        dto.branch = "VIANEN";
        dto.role = "EMPLOYEE";
        dto.employedSince = "2021-01-01";
        dto.supervisorId = null;

        assertThrows(DuplicateRequestException.class, ()  -> service.createPerson(dto));
    }

    @Test
    void createPersonUnknownBranch() {
        PersonRequest dto = new PersonRequest();
        dto.email = "TestEmail@email.com";
        dto.firstname = "fourth";
        dto.lastname = "last";
        dto.expertise = "none";
        dto.branch = "RANDOM_LOCATION";
        dto.role = "EMPLOYEE";
        dto.employedSince = "2021-01-01";
        dto.supervisorId = null;

        assertThrows(IllegalArgumentException.class, ()  -> service.createPerson(dto));
    }

    @Test
    void createPersonUnknownRole() {
        PersonRequest dto = new PersonRequest();
        dto.email = "TestEmail@email.com";
        dto.firstname = "fourth";
        dto.lastname = "last";
        dto.expertise = "none";
        dto.branch = "VIANEN";
        dto.role = "RANDOM_ROLE";
        dto.employedSince = "2021-01-01";
        dto.supervisorId = null;

        assertThrows(IllegalArgumentException.class, ()  -> service.createPerson(dto));
    }

    @Test
    void createPersonUnknownSupervisor() {
        PersonRequest dto = new PersonRequest();
        dto.email = "TestEmail@email.com";
        dto.firstname = "fourth";
        dto.lastname = "last";
        dto.expertise = "none";
        dto.branch = "VIANEN";
        dto.role = "EMPLOYEE";
        dto.employedSince = "2021-01-01";
        dto.supervisorId = UUID.randomUUID();

        assertThrows(NotFoundException.class, ()  -> service.createPerson(dto));
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
        dto.employedSince = "2000-01-01";
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
        try {
            Person person = service.getPersonByEmail("email3@email.com");
            service.removePerson(person.getId());
        } catch (NotFoundException e) {
            fail();
        }
        assertThrows(
                NotFoundException.class
                , () ->service.getPersonByEmail("email3@email.com")
        );
    }

    private static Stream<Arguments> stringRequestExamples() {

        return Stream.of(
                Arguments.of(
                        "tom"
                ),
                Arguments.of(
                        "first"
                ),
                Arguments.of(
                        "second"
                ),
                Arguments.of(
                        "thomaz"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("stringRequestExamples")
    @DisplayName("levenshtein result of person by getting levenshtein value by full, first and lastname")
    void getBestlevenshteinDistanceValue(String req) {
        SearchRequest request = new SearchRequest();
        request.searchTerm = req;
        List<Person> allPersons = this.repo.findAll();
        assertEquals(req, service.getBestLevenshteinDistanceValue(allPersons, request).get(0).getDetails().getFirstname());
    }

    @Test
    @DisplayName("searchPerson provides searched term")
    void searchPerson() {
        SearchRequest request = new SearchRequest();
        request.searchTerm = "thomaz albertorinie";

        List<Person> results = assertDoesNotThrow(
                () -> service.searchPerson(request)
        );
        assertEquals("thomaz",results.get(0).getDetails().getFirstname());
        assertEquals("albertorinie",results.get(0).getDetails().getLastname());
    }

    @Test
    @DisplayName("searchPerson throws when searchTerm is not filled")
    void searchPersonThrows() {
        SearchRequest request = new SearchRequest();
        request.searchTerm = "";

        assertThrows(
                RuntimeException.class,
                () -> service.searchPerson(request)
        );
    }
}