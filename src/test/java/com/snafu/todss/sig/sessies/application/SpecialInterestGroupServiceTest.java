package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import com.snafu.todss.sig.sessies.presentation.dto.request.SpecialInterestGroupRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpecialInterestGroupServiceTest {
    private static final SpecialInterestGroupRepository repository = mock(SpecialInterestGroupRepository.class);
    private static final PersonService personService = mock(PersonService.class);
    private static SpecialInterestGroupService service;
    private static SpecialInterestGroup specialInterestGroup;
    private static Person person;
    private static SpecialInterestGroupRequest request;

    @BeforeAll
    static void beforeAllTests() throws NotFoundException {
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

        when(personService.getPersonByEmail(any()))
                .thenReturn(person);

        when(personService.getPersonById(any()))
                .thenReturn(person);

        specialInterestGroup = new SpecialInterestGroup(
                "any",
                person,
                new ArrayList<>(),
                new ArrayList<>()
        );

        request = new SpecialInterestGroupRequest();
        request.subject = specialInterestGroup.getSubject();
        request.managerId = specialInterestGroup.getManager().getId();
        List<UUID> uuids = new ArrayList<>();
        uuids.add(person.getId());
        request.organizerIds = uuids;
    }

    @BeforeEach
    void setup() {
        service = new SpecialInterestGroupService(repository, personService);
    }

    @AfterEach
    void tearDown() {
        Mockito.clearInvocations(repository, personService);
    }


    @ParameterizedTest
    @MethodSource("provideSpecialInterestGroupExamples")
    @DisplayName("Get all special interest groups")
    void getAllSpecialInterestGroups_ReturnsCorrectSpecialInterestGroups(List<SpecialInterestGroup> expectedResult) {
        when(repository.findAll()).thenReturn(expectedResult);

        List<SpecialInterestGroup> specialInterestGroups = service.getAllSpecialInterestGroups();

        assertEquals(expectedResult, specialInterestGroups);
        verify(repository, times(1)).findAll();
    }

    private static Stream<Arguments> provideSpecialInterestGroupExamples() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(specialInterestGroup)),
                Arguments.of(List.of(specialInterestGroup, new SpecialInterestGroup(
                        "cool subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                ))),
                Arguments.of(List.of(specialInterestGroup, new SpecialInterestGroup(
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
    @DisplayName("Get special interest group by id returns existing special interest group")
    void getSessionById_ReturnsCorrectSession() throws NotFoundException {
        SpecialInterestGroup specialInterestGroupDummy = new SpecialInterestGroup(
                "even cooler subject",
                person,
                new ArrayList<>(),
                new ArrayList<>()
        );
        when(repository.findById(any())).thenReturn(Optional.of(specialInterestGroupDummy));

        SpecialInterestGroup specialInterestGroup = service.getSpecialInterestGroupById(any());

        assertEquals(specialInterestGroupDummy, specialInterestGroup);
        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Get associated employees by SIG id returns list of people")
    void getAssociatedEmployeesById_ReturnsListOfPeople() throws NotFoundException {
        when(repository.findById(any())).thenReturn(Optional.of(specialInterestGroup));

        assertEquals(List.of(person), service.getAssociatedPeopleBySpecialInterestGroup(specialInterestGroup.getId()));
    }

    @Test
    @DisplayName("Get special interest group by id throws when no special interest group was found")
    void getSpecialInterestGroupById_ThrowsWhenDoesNotExist() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getSpecialInterestGroupById(any(UUID.class))
        );
        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Create special interest group method, creates special interest group")
    void createSpecialInterstGroup_CreatesInstance() throws NotFoundException {
        when(repository.save(any(SpecialInterestGroup.class))).thenReturn(specialInterestGroup);

        SpecialInterestGroup specialInterestGroup = service.createSpecialInterestGroup(request);

        assertNotNull(specialInterestGroup);
        verify(personService, times(2)).getPersonById(any());
        verify(repository, times(1)).save(any(SpecialInterestGroup.class));
    }

    @Test
    @DisplayName("Update special interest group, updates special interest group")
    void updateSpecialInterestGroup_CallsMethods() throws NotFoundException {
        when(repository.findById(any())).thenReturn(Optional.of(specialInterestGroup));
        when(repository.save(any(SpecialInterestGroup.class))).thenReturn(specialInterestGroup);

        SpecialInterestGroup specialInterestGroup = service.updateSpecialInterestGroup(UUID.randomUUID(), request);

        assertNotNull(specialInterestGroup);
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(any(SpecialInterestGroup.class));
    }

    @Test
    @DisplayName("Delete special interest group deletes special interest group")
    void deleteSpecialInterestGroup_DeletesSpecialInterestGroup() throws NotFoundException {
        when(repository.existsById(specialInterestGroup.getId())).thenReturn(true);

        service.deleteSpecialInterestGroup(specialInterestGroup.getId());

        verify(repository, times(1)).existsById(specialInterestGroup.getId());
        verify(repository, times(1)).deleteById(specialInterestGroup.getId());
    }

    @Test
    @DisplayName("Delete special interest group does not throw")
    void deleteSpecialInterestGroup_DoesNotThrow() {
        when(repository.existsById(specialInterestGroup.getId())).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteSpecialInterestGroup(specialInterestGroup.getId()));

        verify(repository, times(1)).existsById(specialInterestGroup.getId());
        verify(repository, times(1)).deleteById(specialInterestGroup.getId());
    }

    @Test
    @DisplayName("Delete special interest group with not existing id throws not found")
    void deleteNotExistingSpecialInterestGroup_ThrowsNotFOund() {
        when(repository.existsById(specialInterestGroup.getId())).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.deleteSpecialInterestGroup(UUID.randomUUID())
        );

        verify(repository, times(1)).existsById(any(UUID.class));
    }
}