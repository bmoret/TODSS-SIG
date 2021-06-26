package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.domain.UserRole;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
    private static final UserService userService = mock(UserService.class);
    private static SessionService service;
    private static Session testSession;
    private static Session session;
    private static Session session2;
    private static Session session3;
    private static PhysicalSessionRequest physicalSessionRequest;
    private static Person testPerson;
    private final static Person supervisor = mock(Person.class);
    private final static PersonDetails details = mock(PersonDetails.class);
    private User user;
    private SpecialInterestGroup sig = new SpecialInterestGroup();

    private List<Session> sessions = new ArrayList<>();

    @BeforeAll
    static void init() throws NotFoundException {
        physicalSessionRequest = new PhysicalSessionRequest();
        when(personService.getPerson(any())).thenReturn((testPerson));
    }

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        service = new SessionService(repository, sigService, userService, personService);
        physicalSessionRequest.startDate = now;
        physicalSessionRequest.endDate = nowPlusOneHour;
        physicalSessionRequest.subject = subject;
        physicalSessionRequest.description = description;
        physicalSessionRequest.sigId = UUID.randomUUID();
        physicalSessionRequest.address = address;
        physicalSessionRequest.contactPerson = UUID.randomUUID();

        testSession = new PhysicalSession(
                new SessionDetails(now.plusHours(1), nowPlusOneHour.plusHours(2), subject, description),
                SessionState.PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                supervisor
        );
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
                testPerson
        );
        testPerson = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(testPerson, "id", UUID.randomUUID());
        testPerson.addAttendance(Attendance.of(testPerson, testSession));
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
        Mockito.clearInvocations(repository, sigService, userService, personService);
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
        when(repository.findById(any())).thenReturn(Optional.of(testSession));
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

        when(repository.findById(any())).thenReturn(Optional.of(testSession));
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

        when(repository.findById(any())).thenReturn(Optional.of(testSession));
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
        when(repository.findById(any())).thenReturn(Optional.of(testSession));
        when(repository.save(any(Session.class))).thenReturn(new OnlineSession());
        when(sigService.getSpecialInterestGroupById(request.sigId)).thenReturn(new SpecialInterestGroup());
        UUID id = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateSession(id, request)
        );

        verify(sigService, times(1)).getSpecialInterestGroupById(any(UUID.class));
        verify(repository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Delete session deletes session")
    void deleteSession_DeletesSession() throws NotFoundException {
        when(repository.existsById(testSession.getId())).thenReturn(true);

        service.deleteSession(testSession.getId());

        verify(repository, times(1)).existsById(testSession.getId());
        verify(repository, times(1)).deleteById(testSession.getId());
    }

    @Test
    @DisplayName("Delete session does not throw")
    void deleteSession_DoesNotThrow() {
        when(repository.existsById(testSession.getId())).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteSession(testSession.getId()));

        verify(repository, times(1)).existsById(testSession.getId());
        verify(repository, times(1)).deleteById(testSession.getId());
    }

    @Test
    @DisplayName("Delete session with not existing id throws not found")
    void deleteNotExistingSession_ThrowsNotFOund() {
        when(repository.existsById(testSession.getId())).thenReturn(false);

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
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(testSession));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusHour = LocalDateTime.now().plusHours(1);
        UUID id = UUID.randomUUID();

        assertThrows(
                IllegalStateException.class,
                () -> service.planSession(id, now, nowPlusHour)
        );

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Plan session plans session")
    void planSession_PlansSession() throws NotFoundException {
        testSession = new PhysicalSession(
                new SessionDetails(null, null, "Subject", "Description"),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Address",
                null
        );
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(testSession));

        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime nowPlusHour = LocalDateTime.now().plusHours(2);

        service.planSession(UUID.randomUUID(), now, nowPlusHour);

        verify(repository, times(1)).findById(any(UUID.class));
    }


    @ParameterizedTest
    @MethodSource("provideWrongDates")
    @DisplayName("Plan session with wrong dates throw Illegal Argument Exception")
    void planSessionWithWrongDates_ThrowsIAE(LocalDateTime now, LocalDateTime nowPlusHour) {
        testSession = new PhysicalSession(
                new SessionDetails(null, null, "Subject", "Description"),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Address",
                null
        );
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(testSession));
        UUID id = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.planSession(id, now, nowPlusHour)
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

    @Test
    @DisplayName("get historical sessions is authorized related")
    void historicalSessionsIsAuthorized() throws NotFoundException {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(2));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(1));
        User user = new User("TestUser", "password", testPerson);
        testPerson.addManager(testSession.getSig());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        when(repository.findAll()).thenReturn(List.of(testSession));

        List<Session> sessions = service.getAllHistoricalSessions("user");

        assertTrue(sessions.contains(testSession));
    }

    private void setValuesForIsAuthorized() {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(2));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(1));
        testSession = new PhysicalSession(
                testSession.getDetails(),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "address",
                testPerson
        );
        testPerson.addManager(testSession.getSig());
        when(repository.findAll()).thenReturn(List.of(testSession));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"ROLE_SECRETARY", "ROLE_ADMINISTRATOR"})
    @DisplayName("get historical sessions is authorized to be planned secretary")
    void historicalSessionsIsAuthorizedToBePlanned(UserRole role) throws NotFoundException {
        setValuesForIsAuthorized();
        User user = new User("TestUser", "password", testPerson);
        user.setRole(role);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);

        List<Session> sessions = service.getAllHistoricalSessions("user");

        assertTrue(sessions.contains(testSession));
    }

    @ParameterizedTest
    @MethodSource("historicalSessionsSessions")
    @DisplayName("get historical sessions")
    void historicalSessions(List<Session> sessionsResponses, boolean doesContain) {
        when(repository.findAll()).thenReturn(sessionsResponses);

        List<Session> sessions = service.getAllHistoricalSessions("user");

        assertEquals(doesContain, sessions.contains(sessionsResponses.get(0)));
    }

    static Stream<Arguments> historicalSessionsSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        return Stream.of(
                Arguments.of(List.of(createSession(now.minusHours(2), nowPlusOneHour.minusHours(1), SessionState.ENDED)), true),
                Arguments.of(List.of(createSession(now.minusHours(2), nowPlusOneHour.minusHours(1), SessionState.PLANNED)), true),
                Arguments.of(List.of(createSession(now.minusYears(2), nowPlusOneHour.minusYears(2), SessionState.ENDED)), false),
                Arguments.of(List.of(createSession(now.minusHours(2), nowPlusOneHour.minusHours(1), SessionState.DRAFT)), false),
                Arguments.of(List.of(createSession(now.minusHours(2), nowPlusOneHour.minusHours(1), SessionState.TO_BE_PLANNED)), false)
        );
    }

    private static Session createSession(LocalDateTime start, LocalDateTime end, SessionState state) {
        return testSession = new PhysicalSession(
                new SessionDetails(start, end, "Subject", "Description"),
                state,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Address",
                testPerson
        );
    }

    @ParameterizedTest
    @MethodSource("futureSessionsSessions")
    @DisplayName("get future sessions")
    void futureSessions(Session sessionsResponse, boolean doesContain) {
        when(repository.findAll()).thenReturn(List.of(sessionsResponse));

        List<Session> sessions = service.getAllFutureSessions("user");

        assertEquals(doesContain, sessions.contains(sessionsResponse));
    }

    static Stream<Arguments> futureSessionsSessions() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        return Stream.of(
                Arguments.of(createSession(now, nowPlusOneHour, SessionState.PLANNED), true),
                Arguments.of(createSession(now, nowPlusOneHour, SessionState.ONGOING), true),
                Arguments.of(createSession(now, nowPlusOneHour, SessionState.ENDED), false),
                Arguments.of(createSession(now.minusDays(1), nowPlusOneHour.minusDays(1), SessionState.DRAFT), false)
        );
    }

    private Session setValuesForIsAuthorizedFuture(Session session) {
        session.getDetails().setStartDate(LocalDateTime.now().minusHours(2));
        session.getDetails().setEndDate(LocalDateTime.now().minusHours(1));
        testSession = new PhysicalSession(
                session.getDetails(),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "address",
                testPerson
        );
        when(repository.findAll()).thenReturn(List.of(testSession));
        testPerson.addManager(session.getSig());
        return session;
    }

    @ParameterizedTest
    @MethodSource("futureSessionsSessionsAuthorized")
    @DisplayName("get future sessions is authorized ")
    void futureSessionsIsAuthorizedToBePlanned(Session session, boolean doesContain) throws NotFoundException {
        session = setValuesForIsAuthorizedFuture(session);
        User user = new User("TestUser", "password", testPerson);
        user.setRole(UserRole.ROLE_MANAGER);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(repository.findAll()).thenReturn(List.of(session));

        List<Session> sessions = service.getAllFutureSessions("user");


        assertEquals(doesContain, sessions.contains(session));
    }

    static Stream<Arguments> futureSessionsSessionsAuthorized() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        return Stream.of(
                Arguments.of(createSession(now, nowPlusOneHour, SessionState.TO_BE_PLANNED), true),
                Arguments.of(createSession(now, nowPlusOneHour, SessionState.DRAFT), true),
                Arguments.of(createSession(now.minusDays(1), nowPlusOneHour.minusDays(1), SessionState.ENDED), false)
        );
    }


    @Test
    @DisplayName("get future sessions")
    void futureSessions() {
        when(repository.findAll()).thenReturn(List.of(testSession));

        List<Session> sessions = service.getAllFutureSessions("user");

        assertTrue(sessions.contains(testSession));
    }

    @Test
    @DisplayName("get future sessions is max length 15")
    void futureSessionsMaxLength() {
        List<Session> mockSessions = List.of(
                testSession, testSession, testSession, testSession, testSession,
                testSession, testSession, testSession, testSession, testSession,
                testSession, testSession, testSession, testSession, testSession,
                testSession, testSession, testSession, testSession, testSession,
                testSession, testSession, testSession, testSession, testSession
        );
        when(repository.findAll()).thenReturn(mockSessions);

        List<Session> sessions = service.getAllFutureSessions("user");

        assertEquals(15, sessions.size());
    }

    @Test
    @DisplayName("get future sessions none")
    void futureSessionsNone() {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(1));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(2));
        when(repository.findAll()).thenReturn(List.of(testSession));

        List<Session> sessions = service.getAllFutureSessions("user");

        assertFalse(sessions.contains(testSession));
    }

    @Test
    @DisplayName("get future sessions none")
    void futureSessionsNoUserExisting() throws NotFoundException {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(1));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(2));
        when(repository.findAll()).thenReturn(List.of(testSession));
        when(userService.getUserByUsername(any(String.class))).thenThrow(NotFoundException.class);

        List<Session> sessions = service.getAllFutureSessions("user");

        assertFalse(sessions.contains(testSession));
    }

    @Test
    @DisplayName("get future sessions of person")
    void futureSessionsOfPerson() throws NotFoundException, IllegalAccessException {
        User user = new User("TestUser", "password", testPerson);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);

        List<Session> sessions = service.getFutureSessionsOfPerson(user.getUsername(), testPerson.getId());

        assertTrue(sessions.contains(testSession));
        verify(userService, times(1)).getUserByUsername(any(String.class));
    }

    @Test
    @DisplayName("get future sessions of person as manager")
    void futureSessionsOfPersonAsManager() throws NotFoundException, IllegalAccessException {
        Person person = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(person, "id", UUID.randomUUID());
        testPerson.setSupervisor(person);
        User user = new User("TestUser", "password", person);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);

        List<Session> sessions = service.getFutureSessionsOfPerson(user.getUsername(), testPerson.getId());

        assertTrue(sessions.contains(testSession));
        verify(userService, times(1)).getUserByUsername(any(String.class));
    }

    @Test
    @DisplayName("get future sessions of person not related")
    void futureSessionsOfPersonAsManagerNotRelated() throws NotFoundException {
        Person person = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(person, "id", UUID.randomUUID());
        User user = new User("TestUser", "password", person);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);

        assertThrows(
                IllegalAccessException.class,
                () -> service.getFutureSessionsOfPerson(user.getUsername(), testPerson.getId())
        );

        verify(userService, times(1)).getUserByUsername(any(String.class));
    }

    @Test
    @DisplayName("get future sessions of person not related null")
    void futureSessionsOfPersonAsManagerNotRelatedNull() throws NotFoundException {
        Person person = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(person, "id", UUID.randomUUID());
        Person person2 = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(person2, "id", UUID.randomUUID());
        testPerson.setSupervisor(person2);

        User user = new User("TestUser", "password", person);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);

        assertThrows(
                IllegalAccessException.class,
                () -> service.getFutureSessionsOfPerson(user.getUsername(), testPerson.getId())
        );

        verify(userService, times(1)).getUserByUsername(any(String.class));
    }


    @Test
    @DisplayName("get historical sessions of person")
    void historicalSessionsOfPerson() throws NotFoundException, IllegalAccessException {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(2));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(1));
        User user = setupHistorySessionsOfUser();

        List<Session> sessions = service.getHistorySessionsOfPerson(user.getUsername(), testPerson.getId());

        assertTrue(sessions.contains(testSession));
        verify(userService, times(1)).getUserByUsername(any(String.class));
    }

    private User setupHistorySessionsOfUser() throws NotFoundException {
        User user = new User("TestUser", "password", testPerson);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);
        return user;
    }

    @Test
    @DisplayName("get no historical sessions of person older than this year")
    void noHistoricalSessionsOfPersonOlderThanYear() throws NotFoundException, IllegalAccessException {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusYears(2));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusYears(1));
        User user = setupHistorySessionsOfUser();

        List<Session> sessions = service.getHistorySessionsOfPerson(user.getUsername(), testPerson.getId());

        assertFalse(sessions.contains(testSession));
        verify(userService, times(1)).getUserByUsername(any(String.class));
    }

    @Test
    @DisplayName("get historical sessions of person")
    void historicalSessionsOfPersonAsManager() throws NotFoundException, IllegalAccessException {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(2));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(1));
        Person person = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(person, "id", UUID.randomUUID());
        testPerson.setSupervisor(person);
        User user = new User("TestUser", "password", person);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);

        List<Session> sessions = service.getHistorySessionsOfPerson(user.getUsername(), testPerson.getId());

        assertTrue(sessions.contains(testSession));
        verify(userService, times(1)).getUserByUsername(any(String.class));
    }

    @Test
    @DisplayName("get future sessions of person not related")
    void historicalSessionsOfPersonAsManagerNotRelated() throws NotFoundException {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(2));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(1));
        Person person = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(person, "id", UUID.randomUUID());
        User user = new User("TestUser", "password", person);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);

        assertThrows(
                IllegalAccessException.class,
                () -> service.getHistorySessionsOfPerson(user.getUsername(), testPerson.getId())
        );

        verify(userService, times(1)).getUserByUsername(any(String.class));
    }

    @Test
    @DisplayName("get future sessions of person not related with manager null")
    void historicalSessionsOfPersonAsManagerNotRelatedManagerNull() throws NotFoundException {
        testSession.getDetails().setStartDate(LocalDateTime.now().minusHours(2));
        testSession.getDetails().setEndDate(LocalDateTime.now().minusHours(1));
        Person person = new Person(
                new PersonDetails("", "", "", "", LocalDate.now(), Branch.AMSTERDAM, Role.MANAGER),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        ReflectionTestUtils.setField(person, "id", UUID.randomUUID());
        User user = new User("TestUser", "password", person);
        testPerson.setSupervisor(null);
        when(userService.getUserByUsername(any(String.class))).thenReturn(user);
        when(personService.getPerson(testPerson.getId())).thenReturn(testPerson);

        assertThrows(
                IllegalAccessException.class,
                () -> service.getHistorySessionsOfPerson(user.getUsername(), testPerson.getId())
        );

        verify(userService, times(1)).getUserByUsername(any(String.class));
    }
}