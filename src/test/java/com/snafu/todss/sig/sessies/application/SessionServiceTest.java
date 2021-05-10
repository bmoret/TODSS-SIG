package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionServiceTest {
    private static final SessionRepository repository = mock(SessionRepository.class);
    private static final SpecialInterestGroupService sigService = mock(SpecialInterestGroupService.class);
    private static final PersonService personService = mock(PersonService.class);
    private static SessionService service;
    private static Session session;
    private static PhysicalSessionRequest physicalSessionRequest;
    private static Person supervisor = mock(Person.class);

    @BeforeAll
    static void init() throws NotFoundException {
        physicalSessionRequest = new PhysicalSessionRequest();
        when(personService.getPerson(any())).thenReturn((supervisor));
    }

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        service = new SessionService(repository, sigService, personService);
        physicalSessionRequest.startDate = now;
        physicalSessionRequest.endDate = nowPlusOneHour;
        physicalSessionRequest.subject = subject;
        physicalSessionRequest.description = description;
        physicalSessionRequest.sigId = UUID.randomUUID();
        physicalSessionRequest.address = address;
        physicalSessionRequest.contactPerson = UUID.randomUUID();

        session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                supervisor
        );
    }

    @AfterEach
    void tearDown() {
        Mockito.clearInvocations(repository, sigService);
    }


    @ParameterizedTest
    @MethodSource("provideAllSessionsList")
    @DisplayName("Get all sessions")
    void getAllSessions_ReturnsCorrectSessions(List<Session> expectedResult) {
        when(repository.findAll()).thenReturn(expectedResult);

        List<Session> sessions = service.getAllSessions();

        assertEquals(expectedResult, sessions);
        verify(repository, times(1)).findAll();
    }

    private static Stream<Arguments> provideAllSessionsList() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(new PhysicalSession())),
                Arguments.of(List.of(new PhysicalSession(), new OnlineSession())),
                Arguments.of(List.of(new PhysicalSession(), new OnlineSession(), new TeamsOnlineSession()))
        );
    }

    @Test
    @DisplayName("Get session by id returns existing session")
    void getSessionById_ReturnsCorrectSession() throws NotFoundException {
        Session dummySession = new PhysicalSession();
        when(repository.findById(any())).thenReturn(Optional.of(dummySession));

        Session session = service.getSessionById(any());

        assertEquals(dummySession, session);
        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Get session by id throws when no session with the id was found")
    void getSessionById_ThrowsWhenDoesNotExist() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getSessionById(any(UUID.class))
        );
        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Create session, creates session")
    void createSession_CreatesInstance() throws NotFoundException {
        when(repository.save(any(Session.class))).thenReturn(new PhysicalSession());
        when(sigService.getSpecialInterestGroupById(physicalSessionRequest.sigId)).thenReturn(new SpecialInterestGroup());

        Session resSession = service.createSession(physicalSessionRequest);

        assertNotNull(resSession);
        verify(sigService, times(1)).getSpecialInterestGroupById(any(UUID.class));
        verify(repository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Update session, updates session")
    void updateSession_CallsMethods() throws NotFoundException {
        when(repository.findById(any())).thenReturn(Optional.of(session));
        when(repository.save(any(Session.class))).thenReturn(new PhysicalSession());
        when(sigService.getSpecialInterestGroupById(physicalSessionRequest.sigId)).thenReturn(new SpecialInterestGroup());

        Session resSession = service.updateSession(UUID.randomUUID(), physicalSessionRequest);

        assertNotNull(resSession);
        verify(sigService, times(1)).getSpecialInterestGroupById(any(UUID.class));
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Delete session deletes session")
    void deleteSession_DeletesSession() throws NotFoundException {
        when(repository.existsById(session.getId())).thenReturn(true);

        service.deleteSession(session.getId());

        verify(repository, times(1)).existsById(session.getId());
        verify(repository, times(1)).deleteById(session.getId());
    }

    @Test
    @DisplayName("Delete session does not throw")
    void deleteSession_DoesNotThrow() {
        when(repository.existsById(session.getId())).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteSession(session.getId()));

        verify(repository, times(1)).existsById(session.getId());
        verify(repository, times(1)).deleteById(session.getId());
    }

    @Test
    @DisplayName("Delete session with not existing id throws not found")
    void deleteNotExistingSession_ThrowsNotFOund() {
        when(repository.existsById(session.getId())).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> service.deleteSession(UUID.randomUUID())
        );

        verify(repository, times(1)).existsById(any(UUID.class));
    }

    @Test
    @DisplayName("Plan session that doesnt exist throws not found")
    void planSessionThatDoesNotExist_ThrowsNotFound() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.planSession(UUID.randomUUID(), null, null)
        );

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Plan session that is in the wrong state throws illegalStateException")
    void planSessionThatInWrongState_ThrowsIllegalState() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(session));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusHour = LocalDateTime.now().plusHours(1);

        assertThrows(
                IllegalStateException.class,
                () -> service.planSession(UUID.randomUUID(), now, nowPlusHour)
        );

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Plan session plans session")
    void planSession_PlansSession() throws NotFoundException {
        session = new PhysicalSession(
                new SessionDetails(null, null, "Subject", "Description"),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Address",
                null
        );
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(session));

        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime nowPlusHour = LocalDateTime.now().plusHours(2);

        service.planSession(UUID.randomUUID(), now, nowPlusHour);

        verify(repository, times(1)).findById(any(UUID.class));
    }


    @ParameterizedTest
    @MethodSource("provideWrongDates")
    @DisplayName("Plan session with wrong dates throw Illegal Argument Exception")
    void planSessionWithWrongDates_ThrowsIAE(LocalDateTime now, LocalDateTime nowPlusHour) {
        session = new PhysicalSession(
                new SessionDetails(null, null, "Subject", "Description"),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Address",
                null
        );
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(session));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.planSession(UUID.randomUUID(), now, nowPlusHour)
        );

        verify(repository, times(1)).findById(any(UUID.class));
    }
    private static Stream<Arguments> provideWrongDates() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(2)),
                Arguments.of(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1)),
                Arguments.of(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1)),
                Arguments.of(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                Arguments.of(LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(1)),
                Arguments.of(LocalDateTime.now().plusHours(1), null),
                Arguments.of(null, LocalDateTime.now().minusHours(1)),
                Arguments.of(null, null),
                Arguments.of(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusYears(1)),
                Arguments.of(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusHours(1))
        );
    }
}