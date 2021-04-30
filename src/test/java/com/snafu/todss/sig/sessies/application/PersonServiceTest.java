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
import java.util.ArrayList;
import java.util.List;
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

    private static Stream<Arguments> firstAndLastFillButOnlyFirstExistsExamples() {
        return Stream.of(
                Arguments.of("tom", 3, "tom"),
                Arguments.of("toms", 1, "toms")
        );
    }

    @ParameterizedTest
    @MethodSource("firstAndLastFillButOnlyFirstExistsExamples")
    @DisplayName("search person by using existing firstname and no lastname")
    void searchPersonButOnlyFirstExists(String first, Integer expectedSize, String expectedFirst) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        List<Person> results = service.searchPerson(request);

        for (int i = 0; i < expectedSize; i++) {
            assertEquals(expectedFirst, results.get(i).getDetails().getFirstname());
        }
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> firstAndLastFillButOnlyLastExistsExamples() {
        return Stream.of(
                Arguments.of("alberto", 3, "alberto"),
                Arguments.of("albert", 1, "albert")
        );
    }

    @ParameterizedTest
    @MethodSource("firstAndLastFillButOnlyLastExistsExamples")
    @DisplayName("search person by using existing lastname and no firstname")
    void searchPersonButOnlyLastExists(String last, Integer expectedSize, String expectedLast) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        for (int i = 0; i < expectedSize; i++) {
            assertEquals(expectedLast, results.get(i).getDetails().getLastname());
        }
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> firstAndLastButFirstPartialCorrectExamples() {
        return Stream.of(
                Arguments.of("t", "", 4),
                Arguments.of("eeemaz", "", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("firstAndLastButFirstPartialCorrectExamples")
    @DisplayName("search person by using partial firstname and incorrect lastname")
    void searchPersonButOnlyFirstPartialCorrect(String first, String last, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);
        System.out.println(results.toString());
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> partialFirstAndLastFillExamples() {
        return Stream.of(
                Arguments.of("", "albet", 8),
                Arguments.of("", "albee", 8)
        );
    }

    @ParameterizedTest
    @MethodSource("partialFirstAndLastFillExamples")
    @DisplayName("search person by using partial lastname and incorrect firstname")
    void searchPersonIncorrect(String first, String last, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        assertEquals(expectedSize, results.size());
    }

    @ParameterizedTest
    @MethodSource("firstAndLastButFirstPartialCorrectExamples")
    @DisplayName("search person throws when no results are found with first and lastname given")
    void searchPersonThrowsIfFirstAndLastIncorrect() {
        SearchRequest request = new SearchRequest();
        request.firstname = "xyxyxyxyxyxyxyxyxyxyxy";
        request.lastname = "xyxyxyxyxyxyxyxyxyxyxy";
        assertThrows(
                NotFoundException.class,
                () -> service.searchPerson(request)
        );
    }



    private static Stream<Arguments> wrongFirstWhenSearchingfirstAndLastFillExamples() {
        return Stream.of(
                Arguments.of("toms", "", 1, "toms"),
                Arguments.of("tom", "", 3, "tom")
        );
    }

    @ParameterizedTest
    @MethodSource("wrongFirstWhenSearchingfirstAndLastFillExamples")
    @DisplayName("search person by only useing correct firstname")
    void searchPerson(String first, String last, Integer expectedSize, String expected) throws NotFoundException {
          SearchRequest request = new SearchRequest();
        request.firstname = first;
        request.lastname = last;
        List<Person> results = service.searchPerson(request);

        for (int i = 0; i < expectedSize; i++) {
           assertEquals(expected, results.get(i).getDetails().getFirstname());
        }
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> firstPartialCorrectFillExamples() {
        return Stream.of(
                Arguments.of("t", 4),
                Arguments.of("tee", 6),
                Arguments.of("thomee", 3)
        );
    }

    @ParameterizedTest
    @MethodSource("firstPartialCorrectFillExamples")
    @DisplayName("search person by only using partial correct firstname")
    void searchPersonByFirstname(String first, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        List<Person> results = service.searchPerson(request);
        System.out.println(results.toString());

        assertEquals(expectedSize, results.size());
    }

    @Test
    @DisplayName("search person by only useing correct firstname")
    void searchPersonByFirstnameThrows() {
        SearchRequest request = new SearchRequest();
        request.firstname = "xyxyxyxyxyxyxyxy";
        assertThrows(
                NotFoundException.class,
                () -> service.searchPerson(request)
        );
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

    //firstname only

    private static Stream<Arguments> firstShortFillExamples() {
        return Stream.of(
                Arguments.of("t", 0),
                Arguments.of("ay", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("firstShortFillExamples")
    @DisplayName("search person by short firstname therefore no results")
    void searchPersonByShortFirstname(String first, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.firstname = first;
        List<Person> results = service.searchPerson(request);
        assertEquals(expectedSize, results.size());
    }

    //lastname only
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

    private static Stream<Arguments> lastShortFillExamples() {
        return Stream.of(
                Arguments.of("t", 0),
                Arguments.of("ay", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("lastShortFillExamples")
    @DisplayName("search person by short firstname therefore no results")
    void searchPersonByShortLastname(String last, Integer expectedSize) throws NotFoundException {
        SearchRequest request = new SearchRequest();
        request.lastname = last;
        List<Person> results = service.searchPerson(request);
        assertEquals(expectedSize, results.size());
    }

    //empty search
    @Test
    @DisplayName("not filling out form throws")
    void searchPersonWithEmpty() {
        SearchRequest request = new SearchRequest();
        assertThrows(
                NotFoundException.class,
                () -> service.searchPerson(request)
        );
    }

    @Test
    @DisplayName("compare list filters out doubles")
    void compareLists() throws NotFoundException {
        Person p1 = service.getPersonByEmail("tom@email.com");
        Person p2 = service.getPersonByEmail("tom1@email.com");
        Person p3 = service.getPersonByEmail("tom2@email.com");
        List<Person> list1 = new ArrayList<>();
        List<Person> list2 = new ArrayList<>();
        list1.add(p1);
        list1.add(p2);
        list2.add(p1);
        list2.add(p3);

        List<Person> results = service.compareLists(list1, list2);
        assertEquals(3, results.size());
    }

    private static Stream<Arguments> searchByFirstnameExamples() {
        return Stream.of(
                Arguments.of("tom", 3),
                Arguments.of("toms", 1),
                Arguments.of("", 0),
                Arguments.of(null, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("searchByFirstnameExamples")
    @DisplayName("search person by firstname")
    void searchByFirstname(String firstname, Integer expectedSize) {
        List<Person> results = service.searchByFirstname(firstname);
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> searchByLastnameExamples() {
        return Stream.of(
                Arguments.of("alberto", 3),
                Arguments.of("albert", 1),
                Arguments.of("", 0),
                Arguments.of(null, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("searchByLastnameExamples")
    @DisplayName("search person by lastname")
    void searchByLastname(String lastname, Integer expectedSize) {
        List<Person> results = service.searchByLastname(lastname);
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> searchPersonByPartialExamples() {
        return Stream.of(
                Arguments.of("to", "alb", 5),
                Arguments.of("ms","ert", 1),
                Arguments.of("", "", 0),
                Arguments.of(null, null, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("searchPersonByPartialExamples")
    @DisplayName("search person by partial first and lastname")
    void searchPersonByPartial(String firstname, String lastname, Integer expectedSize) {
        List<Person> results = service.searchPersonByPartial(firstname, lastname);
        System.out.println(results.toString());
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> searchByPartialFirstnameExamples() {
        return Stream.of(
                Arguments.of("tom", 5),
                Arguments.of("ms", 1),
                Arguments.of("eeemaz", 1),
                Arguments.of("", 0),
                Arguments.of(null,0)
        );
    }

    @ParameterizedTest
    @MethodSource("searchByPartialFirstnameExamples")
    @DisplayName("search person by partial firstname")
    void searchPersonByPartialFirstname(String firstname, Integer expectedSize) {
        List<Person> results = service.searchPersonByPartial(firstname, null);
        assertEquals(expectedSize, results.size());
    }

    private static Stream<Arguments> searchByPartialLastnameExamples() {
        return Stream.of(
                Arguments.of("alb", 1),
                Arguments.of("rto", 0),
                Arguments.of("albert", 5),
                Arguments.of("eeeertoreeee", 1),
                Arguments.of("", 0),
                Arguments.of(null, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("searchByPartialLastnameExamples")
    @DisplayName("search person by partial lastname")
    void searchPersonByPartialLastname(String lastname, Integer expectedSize) {
        List<Person> results = service.searchPersonByPartial(null, lastname);
        System.out.println(results);
        assertEquals(expectedSize, results.size());
    }

}