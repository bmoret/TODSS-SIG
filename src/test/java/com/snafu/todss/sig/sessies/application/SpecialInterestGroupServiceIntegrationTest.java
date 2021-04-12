package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.SpecialInterestGroupRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonCompactResponse;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class SpecialInterestGroupServiceIntegrationTest {
    @Autowired
    private SpecialInterestGroupService service;

    @Autowired
    private PersonService personService;

    @Autowired
    private SpecialInterestGroupRepository repository;

    private SpecialInterestGroup specialInterestGroup;

    private static Person person;

    private Person personTwo;

    @BeforeAll
    static void beforeAllTests() {
        PersonBuilder personBuilder = new PersonBuilder();
        personBuilder.setSupervisor(null);
        personBuilder.setBranch(Branch.VIANEN);
        personBuilder.setEmail("email@email.com");
        personBuilder.setExpertise("working");
        personBuilder.setEmployedSince(LocalDate.of(2020, 11, 12));
        personBuilder.setRole(Role.EMPLOYEE);
        personBuilder.setFirstname("napoleon");
        personBuilder.setLastname("dynamite");
        person = personBuilder.build();
    }

    @BeforeEach
    void beforeEachTest() throws NotFoundException {
        PersonRequest supervisorRequest = new PersonRequest();
        supervisorRequest.email = "andereemail@email.com";
        supervisorRequest.firstname = "fourth";
        supervisorRequest.lastname = "last";
        supervisorRequest.expertise = "none";
        supervisorRequest.branch = "VIANEN";
        supervisorRequest.role = "EMPLOYEE";
        supervisorRequest.employedSince = "01/01/2021";
        supervisorRequest.supervisorId = null;
        Person supervisor = personService.createPerson(supervisorRequest);

        PersonRequest dtoPerson = new PersonRequest();
        dtoPerson.email = "andereemail@email.com";
        dtoPerson.firstname = "fourth";
        dtoPerson.lastname = "last";
        dtoPerson.expertise = "none";
        dtoPerson.branch = "VIANEN";
        dtoPerson.role = "EMPLOYEE";
        dtoPerson.employedSince = "01/01/2021";
        dtoPerson.supervisorId = supervisor.getId();

        personTwo = personService.createPerson(dtoPerson);

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
    }

    @ParameterizedTest
    @MethodSource("provideSpecialInterestGroupExamples")
    @DisplayName("Get all special interest groups")
    void getAllSpecialInterestGroups_ReturnsCorrectSpecialInterestGroups(List<SpecialInterestGroup> expectedResult) {
        this.repository.delete(specialInterestGroup);
        expectedResult = this.repository.saveAll(expectedResult);

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
        request.managerId = personTwo.getId();
        List<UUID> uuids = new ArrayList<>();
        uuids.add(personTwo.getId());
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
//    }
}
