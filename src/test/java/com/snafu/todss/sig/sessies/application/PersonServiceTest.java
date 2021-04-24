package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.CiTestConfiguration;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.*;
import com.snafu.todss.sig.sessies.domain.person.enums.*;
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
import java.util.List;
import java.util.stream.Stream;

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

    private static Stream<Arguments> firstAndLastFillExamples() {
        return Stream.of(
                Arguments.of("tom", "albert", 1, "tom", "albert"),
                Arguments.of("tom", "alberto", 2, "tom", "alberto")
        );
    }

    @ParameterizedTest
    @MethodSource("firstAndLastFillExamples")
    @DisplayName("search person by using existing and correct first- and lastname")
    void searchPerson(String first, String last, Integer expectedSize, String expectedFirst, String expectedLast) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        for (int i = 0; i < expectedSize; i++) {
            assertEquals(expectedFirst, results.get(i).getDetails().getFirstname());
            assertEquals(expectedLast, results.get(i).getDetails().getLastname());
        }
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> wrongFirstAndLastFillExamples() {
        return Stream.of(
                Arguments.of("t", "albet", 0),
                Arguments.of("a", "b", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("wrongFirstAndLastFillExamples")
    @DisplayName("search person by using non-existing first- and lastname")
    void searchPersonIncorrect(String first, String last, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> wrongFirstWhenSearchingfirstAndLastFillExamples() {
        return Stream.of(
                Arguments.of("toms", "gfhfgh", 1, "toms"),
                Arguments.of("tom", "dfgdfgdfg", 3, "tom")
        );
    }

    @ParameterizedTest
    @MethodSource("wrongFirstWhenSearchingfirstAndLastFillExamples")
    @DisplayName("search person by using existing firstname and incorrect lastname")
    void searchPerson(String first, String last, Integer expectedSize, String expected) throws NotFoundException {
        System.out.println(first);
        System.out.println(last);
        System.out.println(expectedSize);
        System.out.println(expected);

        SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        for (int i = 0; i < expectedSize; i++) {
           assertEquals(expected, results.get(i).getDetails().getFirstname());
        }
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> wrongLastWhenSearchingfirstAndLastFillExamples() {
        return Stream.of(
                Arguments.of("dfsdf", "albert", 1, "albert"),
                Arguments.of("sdfsf", "alberto", 3, "alberto")
        );
    }

    @ParameterizedTest
    @MethodSource("wrongLastWhenSearchingfirstAndLastFillExamples")
    @DisplayName("search person by using existing first- and lastname")
    void searchPersonIncorrectLast(String first, String last, Integer expectedSize, String expected) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        for (int i = 0; i < expectedSize; i++) {
            assertEquals(expected, results.get(i).getDetails().getLastname());
        }
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> wrongCombiWhenSearchingfirstAndLastFillExamples() {
        return Stream.of(
                Arguments.of("tom", "alber", 4),
                Arguments.of("to", "albert", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("wrongCombiWhenSearchingfirstAndLastFillExamples")
    @DisplayName("search person by using existing first- and lastname but dont make one person")
    void searchPersonIncorrectCombinationOfExistingFirstLast(String first, String last, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        assertEquals(expectedSize, results.size());
    }


    private static Stream<Arguments> firstFillExamples() {
        return Stream.of(
                Arguments.of("tom", 3),
                Arguments.of("toms", 1),
                Arguments.of("a", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("firstFillExamples")
    @DisplayName("search person by only using existing firstname")
    void searchPersonByFirstname(String first, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        List<Person> results = service.searchPerson(request);
        for(int i = 0; i < expectedSize; i++) {
            assertEquals(first, results.get(i).getDetails().getFirstname());
        }
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> lastFillExamples() {
        return Stream.of(
                Arguments.of("alberto", 3),
                Arguments.of("albert", 1),
                Arguments.of("a", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("lastFillExamples")
    @DisplayName("search person by only using existing lastname")
    void searchPersonByLastname(String last, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.lastname = last;
        List<Person> results = service.searchPerson(request);
        for(int i = 0; i < expectedSize; i++) {
            assertEquals(last, results.get(i).getDetails().getLastname());
        }
        assertEquals(expectedSize, results.size());
    }

    @Test
    @DisplayName("search person by only using existing lastname")
    void searchPersonWithEmpty() {
        SearchRequest request = new SearchRequest();
        assertThrows(
                NotFoundException.class,
                () -> service.searchPerson(request)
        );
    }
}