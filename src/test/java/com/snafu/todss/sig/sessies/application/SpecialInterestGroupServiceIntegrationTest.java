package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.CiTestConfiguration;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.SpecialInterestGroupRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
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
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@Import(CiTestConfiguration.class)
@SpringBootTest
class SpecialInterestGroupServiceIntegrationTest {
    @Autowired
    private SpecialInterestGroupService service;

    @Autowired
    private PersonService personService;

    @Autowired
    private SpecialInterestGroupRepository repository;

    @Autowired
    private SpringPersonRepository personRepository;

    private SpecialInterestGroup specialInterestGroup;

    private static Person supervisor;

    private static Person person;


    @BeforeEach
    void setup() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test2@email.com";
        dtoSupervisor.firstname = "fourth";
        dtoSupervisor.lastname = "last";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "01/01/2021";
        dtoSupervisor.supervisorId = null;
        supervisor = personService.createPerson(dtoSupervisor);

        PersonRequest dtoPerson = new PersonRequest();
        dtoPerson.email = "andereemail@email.com";
        dtoPerson.firstname = "fourth";
        dtoPerson.lastname = "last";
        dtoPerson.expertise = "none";
        dtoPerson.branch = "VIANEN";
        dtoPerson.role = "EMPLOYEE";
        dtoPerson.employedSince = "01/01/2021";
        dtoPerson.supervisorId = supervisor.getId();
        person = personService.createPerson(dtoPerson);

        specialInterestGroup = this.repository.save(new SpecialInterestGroup(
                    "any",
                    person,
                    new ArrayList<>(),
                    new ArrayList<>()
        ));

    }

    @AfterEach
    void afterEachTest() {
        this.repository.deleteAll();
        this.personRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideSpecialInterestGroupExamples")
    @DisplayName("Get all special interest groups")
    void getAllSpecialInterestGroups_ReturnsCorrectSpecialInterestGroups(List<SpecialInterestGroup> expectedResult) {
        repository.deleteAll();
        for (SpecialInterestGroup sig : expectedResult) {
            sig.setManager(person);
            this.repository.save(sig);
        }

        List<SpecialInterestGroup> specialInterestGroups = service.getAllSpecialInterestGroups();

        assertEquals(expectedResult.size(), specialInterestGroups.size());
        assertTrue(specialInterestGroups.containsAll(expectedResult));
    }

    private static Stream<Arguments> provideSpecialInterestGroupExamples() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(new SpecialInterestGroup(
                        "cool subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                ))),
                Arguments.of(List.of(new SpecialInterestGroup(
                        "cool subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                ), new SpecialInterestGroup(
                        "even cooler subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                )))
        );
    }

    @Test
    @DisplayName("Get special interest group by id returns special interest group")
    void getSpecialInterestGroupById_ReturnsSpecialInterestGroup() throws NotFoundException {
        SpecialInterestGroup sig = service.getSpecialInterestGroupById(specialInterestGroup.getId());
        assertEquals(sig, specialInterestGroup);
    }

    @Test
    @DisplayName("Get associated employees by SIG id returns list of people")
    void getAssociatedEmployeesById_ReturnsListOfPeople() throws NotFoundException {
        specialInterestGroup.addOrganizer(supervisor);
        List<Person> people = service.getAssociatedPeopleBySpecialInterestGroup(specialInterestGroup.getId());

        assertEquals(2, people.size());
        assertTrue(people.contains(person));
        assertTrue(people.contains(supervisor));
    }

    @Test
    @DisplayName("Get special interest group by id when no special interest group exists with id throw")
    void getNotExistingSpecialInterestGroupById_Throws() {
        assertThrows(
                NotFoundException.class,
                () -> service.getSpecialInterestGroupById(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Creating a special interest group returns a newly made special interest group")
    void createSpecialInterestGroup_CreatesInstance() throws NotFoundException {
        SpecialInterestGroupRequest request = new SpecialInterestGroupRequest();
        request.subject = specialInterestGroup.getSubject();
        request.managerId = person.getId();
        List<UUID> uuids = new ArrayList<>();
        uuids.add(person.getId());
        request.organizerIds = uuids;

        SpecialInterestGroup session = service.createSpecialInterestGroup(request);

        assertEquals(session.getClass(), SpecialInterestGroup.class);
    }

    @Test
    @DisplayName("Deleting special interest group deletes special interest group")
    void deleteSpecialInterestGroup_DeletesSpecialInterestGroup() throws NotFoundException {
        service.deleteSpecialInterestGroup(specialInterestGroup.getId());
        assertEquals(Collections.emptyList(), repository.findAll());
    }

    @Test
    @DisplayName("Deleting special interest group does not throw")
    void deleteSpecialInterestGroup_DoesNotThrow() {
        assertDoesNotThrow(() -> service.deleteSpecialInterestGroup(specialInterestGroup.getId()));
    }
}
