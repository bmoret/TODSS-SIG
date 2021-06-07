package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.CiTestConfiguration;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.sun.jdi.request.DuplicateRequestException;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
    @DisplayName("getPersonByEmail returns person")
    void getPersonByEmail() throws NotFoundException {
        Person person = service.getPersonByEmail("email2@email.com");
        assertEquals("email2@email.com",person.getDetails().getEmail());
        assertEquals("second",person.getDetails().getFirstname());
        assertEquals("last",person.getDetails().getLastname());
        assertEquals("none",person.getDetails().getExpertise());
        assertEquals(LocalDate.of(2000,1,1),person.getDetails().getEmployedSince());
        assertEquals(Branch.VIANEN,person.getDetails().getBranch());
        assertEquals(Role.EMPLOYEE,person.getDetails().getRole());
        assertEquals("first",person.getSupervisor().getDetails().getFirstname());
    }

    @Test
    @DisplayName("getPersonByEmail throws when no results are found")
    void getPersonByEmailThrows() {
        assertThrows(
                NotFoundException.class,
                () -> service.getPersonByEmail("hoihoi@email.com")
        );
    }

    @Test
    @DisplayName("Create person")
    void createPerson() throws NotFoundException {
        Person supervisor = service.getPersonByEmail("email@email.com");;
        PersonRequest createRequest = new PersonRequest();
        createRequest.email = "email4@email.com";
        createRequest.firstname = "fourth";
        createRequest.lastname = "last";
        createRequest.expertise = "none";
        createRequest.branch = "VIANEN";
        createRequest.role = "EMPLOYEE";
        createRequest.employedSince = "2021-01-01";
        createRequest.supervisorId = supervisor.getId();

        Person person = service.createPerson(createRequest);

        assertEquals("email4@email.com", person.getDetails().getEmail());
        assertEquals("fourth", service.getPersonByEmail("email4@email.com").getDetails().getFirstname());
    }

    @Test
    @DisplayName("Create person with already used email")
    void createPersonDuplicateEmail() {
        PersonRequest createRequest = new PersonRequest();
        createRequest.email = "email2@email.com";
        createRequest.firstname = "fourth";
        createRequest.lastname = "last";
        createRequest.expertise = "none";
        createRequest.branch = "VIANEN";
        createRequest.role = "EMPLOYEE";
        createRequest.employedSince = "2021-01-01";
        createRequest.supervisorId = null;

        assertThrows(
                DuplicateRequestException.class,
                ()  -> service.createPerson(createRequest)
        );
    }

    @Test
    @DisplayName("Create person with not existing branch")
    void createPersonUnknownBranch() {
        PersonRequest createRequest = new PersonRequest();
        createRequest.email = "TestEmail@email.com";
        createRequest.firstname = "fourth";
        createRequest.lastname = "last";
        createRequest.expertise = "none";
        createRequest.branch = "RANDOM_LOCATION";
        createRequest.role = "EMPLOYEE";
        createRequest.employedSince = "2021-01-01";
        createRequest.supervisorId = null;

        assertThrows(
                IllegalArgumentException.class,
                ()  -> service.createPerson(createRequest)
        );
    }

    @Test
    @DisplayName("Create person with not existing role")
    void createPersonUnknownRole() {
        PersonRequest createRequest = new PersonRequest();
        createRequest.email = "TestEmail@email.com";
        createRequest.firstname = "fourth";
        createRequest.lastname = "last";
        createRequest.expertise = "none";
        createRequest.branch = "VIANEN";
        createRequest.role = "RANDOM_ROLE";
        createRequest.employedSince = "2021-01-01";
        createRequest.supervisorId = null;

        assertThrows(
                IllegalArgumentException.class,
                ()  -> service.createPerson(createRequest)
        );
    }

    @Test
    @DisplayName("Create person with not existing supervisor")
    void createPersonUnknownSupervisor() {
        PersonRequest createRequest = new PersonRequest();
        createRequest.email = "TestEmail@email.com";
        createRequest.firstname = "fourth";
        createRequest.lastname = "last";
        createRequest.expertise = "none";
        createRequest.branch = "VIANEN";
        createRequest.role = "EMPLOYEE";
        createRequest.employedSince = "2021-01-01";
        createRequest.supervisorId = UUID.randomUUID();

        assertThrows(
                NotFoundException.class,
                ()  -> service.createPerson(createRequest)
        );
    }

    @Test
    @DisplayName("Edit person")
    void editPerson() throws NotFoundException {
        Person supervisor = service.getPersonByEmail("email@email.com");
        PersonRequest editRequest = new PersonRequest();
        editRequest.email = "email2@email.com";
        editRequest.firstname = "second";
        editRequest.lastname = "last";
        editRequest.expertise = "all";
        editRequest.branch = "VIANEN";
        editRequest.role = "EMPLOYEE";
        editRequest.employedSince = "2000-01-01";
        editRequest.supervisorId = supervisor.getId();

        Person person = service.editPerson(
                service.getPersonByEmail("email2@email.com").getId(),
                editRequest
        );
        assertEquals("email2@email.com", person.getDetails().getEmail());
        assertEquals("all", person.getDetails().getExpertise());
        assertEquals("second", service.getPersonByEmail("email2@email.com").getDetails().getFirstname());
        assertEquals("all", service.getPersonByEmail("email2@email.com").getDetails().getExpertise());

    }

    @Test
    @DisplayName("Remove not existing person by email throws")
    void removePerson() throws NotFoundException {
        Person person = service.getPersonByEmail("email3@email.com");
        service.removePerson(person.getId());
        assertThrows(
                NotFoundException.class,
                () ->service.getPersonByEmail("email3@email.com")
        );
    }

    private static Stream<Arguments> stringRequestExamples() {
        return Stream.of(
                Arguments.of("tom"),
                Arguments.of("first"),
                Arguments.of("second"),
                Arguments.of("thomaz")
        );
    }

    @ParameterizedTest
    @MethodSource("stringRequestExamples")
    @DisplayName("levenshtein result of person by getting levenshtein value by full, first and lastname")
    void getBestLevenshteinDistanceValue(String req) {
        List<Person> allPersons = this.repo.findAll();
        assertEquals(
                req,
                service.getBestLevenshteinDistanceValue(allPersons, req).get(0).getDetails().getFirstname()
        );
    }

    @Test
    @DisplayName("levenshtein result of person by getting levenshtein value by full, first and lastname")
    void getBestLevenshteinDistanceValueError() {
        List<Person> allPersons = this.repo.findAll();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getBestLevenshteinDistanceValue(allPersons, ""));
    }

    @Test
    @DisplayName("searchPerson provides searched term")
    void searchPerson() {
        List<Person> results = assertDoesNotThrow(
                () -> service.searchPerson("thomaz albertorinie")
        );

        assertEquals("thomaz",results.get(0).getDetails().getFirstname());
        assertEquals("albertorinie",results.get(0).getDetails().getLastname());
    }

    @Test
    @DisplayName("searchPerson throws when searchTerm is not filled")
    void searchPersonThrows() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchPerson("")
        );
    }
}