package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.domain.UserRole;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.OnlineSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
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
import org.springframework.security.core.userdetails.UserDetails;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class SessionServiceIntegrationTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository repository;

    @Autowired
    private SpecialInterestGroupRepository sigRepository;

    @Autowired
    private SpringPersonRepository personRepository;

    @Autowired
    private SpringUserRepository userRepository;

    private Session testSession;

    private Session testSession2;

    private Session testSession3;

    private Person supervisor;

    private User user;

    private SpecialInterestGroup sig;


    @BeforeEach
    void setup() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test3@email.com";
        dtoSupervisor.firstname = "fourth";
        dtoSupervisor.lastname = "last";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2005-12-01";
        dtoSupervisor.supervisorId = null;
        supervisor = personService.createPerson(dtoSupervisor);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        sig = sigRepository.save(new SpecialInterestGroup());
        this.testSession = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address,
                        supervisor
                )
        );

        testSession2 = repository.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.TO_BE_PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address,
                        null
                )
        );

        testSession3 = repository.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address,
                        null
                )
        );

        user = userRepository.save(new User("TestUser", "TestPassword", supervisor));
    }

    @AfterEach
    void tearDown() {
        this.repository.deleteAll();
        this.personRepository.deleteAll();
    }

    @Test
    @DisplayName("Get all sessions as a manager when the SIG he is managing has 3 sessions")
    void getAllSessionsWhenManagingSIG_AsManager(){
        user.setRole(UserRole.ROLE_MANAGER);
        user.getPerson().addManager(sig);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(3, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as a manager when they are not managing a SIG")
    void getAllSessionsWhenNotManaging_AsManager(){
        user.setRole(UserRole.ROLE_MANAGER);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as an organizer when the SIG they are organizing has 3 sessions")
    void getAllSessionsWhenOrganizingSIG_AsOrganizer(){
        user.setRole(UserRole.ROLE_ORGANIZER);
        user.getPerson().addOrganizer(sig);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(3, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as a manager when they are not organizing a SIG")
    void getAllSessionsWhenNotOrganizingSIG_AsOrganizer(){
        user.setRole(UserRole.ROLE_ORGANIZER);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as a manager when they are both organizing and managing a SIG that has three sessions")
    void getAllSessionsWhenOrganizingAndManagingSIG_AsManager(){
        user.setRole(UserRole.ROLE_MANAGER);
        user.getPerson().addOrganizer(sig);
        user.getPerson().addManager(sig);

        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(3, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as a manager when managing multiple SIGs")
    void getAllSessionsWhenManagingMultipleSIG_AsManager(){
        user.setRole(UserRole.ROLE_ORGANIZER);
        user.getPerson().addManager(sig);
        SpecialInterestGroup sig1 = sigRepository.save(new SpecialInterestGroup());
        user.getPerson().addManager(sig1);

        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(3, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as a secretary")
    void getAllSessions_AsSecretary(){
        user.setRole(UserRole.ROLE_SECRETARY);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(2, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as an administrator")
    void getAllSessions_AsAdministrator(){
        user.setRole(UserRole.ROLE_ADMINISTRATOR);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(3, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as an employee")
    void getAllSessions_AsEmployee(){
        user.setRole(UserRole.ROLE_EMPLOYEE);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Get all sessions as a guest")
    void getAllSessions_AsGuest(){
        user.setRole(UserRole.ROLE_GUEST);
        List<Session> sessions = sessionService.getAllSessions(user);

        assertEquals(1, sessions.size());
    }

    @Test
    @DisplayName("Get session by id returns session")
    void getSessionById_ReturnsSession() throws NotFoundException {
        Session session = sessionService.getSessionById(testSession.getId());
        assertEquals(testSession, session);
    }

    @Test
    @DisplayName("Get session by id when no session exists with id throw")
    void getNotExistingSessionById_Throws() {
        assertThrows(
                NotFoundException.class,
                () -> sessionService.getSessionById(UUID.randomUUID())
        );
    }

    @ParameterizedTest
    @MethodSource("provideCreateSessionArgs")
    @DisplayName("Creating a session returns a newly made session")
    void createSession_CreatesInstance(SessionRequest request, Class<Session> expectedClass) throws NotFoundException {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        request.sigId = sig.getId();

        Session session = sessionService.createSession(request);

        assertTrue(expectedClass.isInstance(session));
    }

    private static Stream<Arguments> provideCreateSessionArgs() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        return Stream.of(
                Arguments.of(
                        new PhysicalSessionRequest(
                                now,
                                nowPlusOneHour,
                                "Subject",
                                "Description",
                                UUID.randomUUID(),
                                "Address",
                                null
                        ),
                        PhysicalSession.class
                ),
                Arguments.of(
                        new OnlineSessionRequest(
                                now,
                                nowPlusOneHour,
                                "Subject",
                                "Description",
                                UUID.randomUUID(),
                                "Random Platform",
                                "link",
                                null
                        ),
                        OnlineSession.class
                ),
                Arguments.of(
                        new OnlineSessionRequest(
                                now, nowPlusOneHour,
                                "Subject",
                                "Description",
                                UUID.randomUUID(),
                                "Teams",
                                "link",
                                null
                        ),
                        TeamsOnlineSession.class
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCreateSessionArgs")
    @DisplayName("Creating a session does not throw")
    void createSession_DoesNotThrow(SessionRequest request){
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        request.sigId = sig.getId();

        assertDoesNotThrow(() -> sessionService.createSession(request));
    }

    @Test
    @DisplayName("Creating session with not existing special interest group throws")
    void createSessionWithNotExistingSig_ThrowsNotFound() {
        PhysicalSessionRequest request = new PhysicalSessionRequest(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Address",
                null
        );

        assertThrows(
                NotFoundException.class,
                () -> sessionService.createSession(request)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCreateSessionArgs")
    @DisplayName("Updating a session does not throw")
    void updateSession_DoesNotThrow(SessionRequest request) throws NotFoundException {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        request.sigId = sig.getId();
        Session session = sessionService.createSession(request);
        request.subject = "New Subject";
        request.contactPerson = supervisor.getId();

        assertDoesNotThrow(() -> sessionService.updateSession(session.getId(), request));
    }

    @ParameterizedTest
    @MethodSource("provideCreateSessionArgs")
    @DisplayName("Updating a session returns the updated session")
    void updateSession_CreatesInstance(SessionRequest request) throws NotFoundException {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        request.sigId = sig.getId();
        Session session = sessionService.createSession(request);
        request.subject = "New Subject";
        request.contactPerson = supervisor.getId();

        session = sessionService.updateSession(session.getId(), request);

        assertEquals(request.subject, session.getDetails().getSubject());
        assertEquals(supervisor, session.getContactPerson());
    }

    @Test
    @DisplayName("Updating session with not existing special interest group throws")
    void updateSessionWithNotExistingSig_ThrowsNotFound() {
        PhysicalSessionRequest request = new PhysicalSessionRequest(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Address",
                supervisor.getId().toString()
        );

        assertThrows(
                NotFoundException.class,
                () -> sessionService.updateSession(testSession.getId(), request)
        );
    }

    private PhysicalSessionRequest providePhysicalSessionRequest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);

        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());

        return new PhysicalSessionRequest(
                now,
                nowPlusOneHour,
                "Subject",
                "Description",
                sig.getId(),
                "Address",
                supervisor.getId().toString()
        );
    }
    private OnlineSessionRequest provideOnlineSessionRequest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);

        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());

        return new OnlineSessionRequest(
                now,
                nowPlusOneHour,
                "Subject",
                "Description",
                sig.getId(),
                "Random Platform",
                "link",
                supervisor.getId().toString()
        );
    }

    @Test
    @DisplayName("Updating session type from online to physical")
    void updateSessionFromOnlineToPhysical() {
        Session onlineSession = assertDoesNotThrow(
                () -> sessionService.createSession(provideOnlineSessionRequest())
        );
        System.out.println(repository.findAll().size());
        Session updatedSession = assertDoesNotThrow(
                () -> sessionService.updateSession(onlineSession.getId(), providePhysicalSessionRequest())
        );
        System.out.println(repository.findAll().size());
        System.out.println("na save "+ updatedSession.getId());
        assertEquals(onlineSession.getId(), updatedSession.getId());
        assertEquals(onlineSession.getAttendances().size(), updatedSession.getAttendances().size());
        assertEquals(onlineSession.getFeedback().size(), updatedSession.getFeedback().size());

    }

    @Test
    @DisplayName("Deleting session deletes session")
    void deleteSession_DeletesSession() throws NotFoundException {
        sessionService.deleteSession(testSession.getId());
        sessionService.deleteSession(testSession2.getId());
        sessionService.deleteSession(testSession3.getId());

        assertEquals(Collections.emptyList(), repository.findAll());
    }

    @Test
    @DisplayName("Deleting session does not throw")
    void deleteSession_DoesNotThrow() {
        assertDoesNotThrow(() -> sessionService.deleteSession(testSession.getId()));
    }

    @Test
    @DisplayName("Request Not existing session to be planned throws not found")
    void requestNotExistingSessionToBePlanned_ThrowsNotFound() {
        assertThrows(
                NotFoundException.class,
                () -> sessionService.requestSessionToBePlanned(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Request Not existing session to be planned throws not found")
    void requestSessionToBePlannedWithWrongState_ThrowsIAE() {
        testSession.nextState();
        repository.save(testSession);

        assertThrows(
                IllegalStateException.class,
                () -> sessionService.requestSessionToBePlanned(testSession.getId())
        );
    }

    @Test
    @DisplayName("Request session to be planned requests planning")
    void requestSessionToBePlanned_RequestsPlanning() throws NotFoundException {
        Session session = sessionService.requestSessionToBePlanned(testSession.getId());

        assertEquals(SessionState.TO_BE_PLANNED, session.getState());
    }

    @Test
    @DisplayName("Plan session that doesnt exist throws not found")
    void planSessionThatDoesNotExist_ThrowsNotFound() {
        assertThrows(
                NotFoundException.class,
                () -> sessionService.planSession(UUID.randomUUID(), null, null)
        );
    }

    @Test
    @DisplayName("Plan session that is in the wrong state throws illegalStateException")
    void planSessionThatInWrongState_ThrowsIllegalState() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusHour = LocalDateTime.now().plusHours(1);

        assertThrows(
                IllegalStateException.class,
                () -> sessionService.planSession(testSession.getId(), now, nowPlusHour)
        );
    }

    @Test
    @DisplayName("Plan session plans session")
    void planSession_PlansSession() throws NotFoundException {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        this.testSession = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(null, null, "Subject", "Description"),
                        SessionState.TO_BE_PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        "Address",
                        null
                )
        );
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        LocalDateTime nowPlusHour = LocalDateTime.now().plusHours(2);

        Session session = sessionService.planSession(testSession.getId(), now, nowPlusHour);

        assertEquals(SessionState.PLANNED, session.getState());
    }


    @ParameterizedTest
    @MethodSource("provideWrongDates")
    @DisplayName("Plan session with wrong dates throw Illegal Argument Exception")
    void planSessionWithWrongDates_ThrowsIAE(LocalDateTime now, LocalDateTime nowPlusHour) {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        this.testSession = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(null, null, "Subject", "Description"),
                        SessionState.TO_BE_PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        "Address",
                        null
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> sessionService.planSession(testSession.getId(), now, nowPlusHour)
        );
    }
    private static Stream<Arguments> provideWrongDates() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(2)),
                Arguments.of(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1)),
                Arguments.of(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1)),
                Arguments.of(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)),
                Arguments.of(LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(1))
        );
    }
}