package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.domain.UserRole;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.OnlineSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
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
    private static Session session2;
    private static Session session3;
    private static PhysicalSessionRequest physicalSessionRequest;
    private final static Person supervisor = mock(Person.class);
    private final static PersonDetails details = mock(PersonDetails.class);
    private User user;
    private SpecialInterestGroup sig = new SpecialInterestGroup();

    private List<Session> sessions = new ArrayList<>();

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
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                supervisor
        );
        session2 = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.TO_BE_PLANNED,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                supervisor
        );
        session3 = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.PLANNED,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                supervisor
        );
        sessions.add(session);
        sessions.add(session2);
        sessions.add(session3);

        when(supervisor.getDetails()).thenReturn(details);
        when(details.getRole()).thenReturn(Role.MANAGER);

        user = new User("TestUser", "TestPassword", supervisor);

    }

    @AfterEach
    void tearDown() {
        Mockito.clearInvocations(repository, sigService);
    }

    @Test
    @DisplayName("Get all sessions as manager when not managing SIG")
    void getAllSessionsWhenNotManaging_AsManager(){
        when(repository.findAll()).thenReturn((sessions));
        when(supervisor.getManagedSpecialInterestGroups()).thenReturn((List.of()));

        user.setRole(UserRole.ROLE_MANAGER);

        List<Session> sessions = service.getAllSessions(user);

        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as manager when the SIG they are managing has three sessions")
    void getAllSessionsWhenManaging_AsManager(){
        when(repository.findAll()).thenReturn((sessions));
        when(supervisor.getManagedSpecialInterestGroups()).thenReturn((List.of(sig)));

        user.setRole(UserRole.ROLE_MANAGER);

        List<Session> sessions = service.getAllSessions(user);

        assertEquals(3, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as organizer when not organizing SIG")
    void getAllSessionsWhenNotOrganizing_AsOrganizer(){
        when(repository.findAll()).thenReturn((sessions));
        when(supervisor.getManagedSpecialInterestGroups()).thenReturn((List.of()));

        user.setRole(UserRole.ROLE_ORGANIZER);

        List<Session> sessions = service.getAllSessions(user);

        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as organizer when the SIG they are organizing has three sessions")
    void getAllSessionsWhenOrganizing_AsOrganizer(){
        when(repository.findAll()).thenReturn((sessions));
        when(supervisor.getOrganisedSpecialInterestGroups()).thenReturn((List.of(sig)));

        user.setRole(UserRole.ROLE_MANAGER);

        List<Session> sessions = service.getAllSessions(user);

        assertEquals(3, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as secretary")
    void getAllSessions_AsSecretary(){
        when(repository.findAll()).thenReturn((sessions));

        user.setRole(UserRole.ROLE_SECRETARY);

        List<Session> sessions = service.getAllSessions(user);

        assertEquals(2, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as employee")
    void getAllSessions_AsEmployee(){
        when(repository.findAll()).thenReturn((sessions));

        user.setRole(UserRole.ROLE_EMPLOYEE);

        List<Session> sessions = service.getAllSessions(user);

        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as guest")
    void getAllSessions_AsGuest(){
        when(repository.findAll()).thenReturn((sessions));

        user.setRole(UserRole.ROLE_EMPLOYEE);

        List<Session> sessions = service.getAllSessions(user);

        assertEquals(1, sessions.size());
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
    @DisplayName("Update session, from Physical to Online")
    void updateSessionFromPhysicalToOnline() throws NotFoundException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);

        OnlineSessionRequest onlineSessionRequest = new OnlineSessionRequest(
                now,
                nowPlusOneHour,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Random Platform",
                "link",
                UUID.randomUUID().toString()
        );

        when(repository.findById(any())).thenReturn(Optional.of(session));
        when(repository.save(any(Session.class))).thenReturn(new OnlineSession());
        when(sigService.getSpecialInterestGroupById(onlineSessionRequest.sigId)).thenReturn(new SpecialInterestGroup());

        Session resSession = service.updateSession(UUID.randomUUID(), onlineSessionRequest);

        assertNotNull(resSession);
        verify(sigService, times(1)).getSpecialInterestGroupById(any(UUID.class));
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Update session, from Online to Physical")
    void updateSessionFromOnlineToPhysical() throws NotFoundException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);

        PhysicalSessionRequest physicalSessionRequest = new PhysicalSessionRequest(
                now,
                nowPlusOneHour,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Address",
                UUID.randomUUID().toString()
        );

        when(repository.findById(any())).thenReturn(Optional.of(session));
        when(repository.save(any(Session.class))).thenReturn(new OnlineSession());
        when(sigService.getSpecialInterestGroupById(physicalSessionRequest.sigId)).thenReturn(new SpecialInterestGroup());

        Session resSession = service.updateSession(UUID.randomUUID(), physicalSessionRequest);

        assertNotNull(resSession);
        verify(sigService, times(1)).getSpecialInterestGroupById(any(UUID.class));
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Update session type throws when correct type is not found")
    void updateSessionFromTypeThrows() throws NotFoundException {
        SessionRequest request = mock(SessionRequest.class);
        request.sigId = UUID.randomUUID();
        when(repository.findById(any())).thenReturn(Optional.of(session));
        when(repository.save(any(Session.class))).thenReturn(new OnlineSession());
        when(sigService.getSpecialInterestGroupById(request.sigId)).thenReturn(new SpecialInterestGroup());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateSession(UUID.randomUUID(), request)
        );

        verify(sigService, times(1)).getSpecialInterestGroupById(any(UUID.class));
        verify(repository, times(1)).findById(any());
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