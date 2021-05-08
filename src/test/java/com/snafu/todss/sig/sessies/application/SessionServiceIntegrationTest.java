package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
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
    private SpecialInterestGroupService sigService;

    @Autowired
    private PersonService personService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository repository;

    @Autowired
    private SpecialInterestGroupRepository sigRepository;

    private Session testSession;

    private Person supervisor;

    @BeforeEach
    void setup() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "email@email.com";
        dtoSupervisor.firstname = "fourth";
        dtoSupervisor.lastname = "last";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "01/01/2021";
        dtoSupervisor.supervisorId = null;
        supervisor = personService.createPerson(dtoSupervisor);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
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
    }

    @AfterEach
    void tearDown() {
        this.repository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideAllSessionsList")
    @DisplayName("Get all sessions")
    void getAllSessions_ReturnsCorrectSessions(List<Session> expectedResult) {
        this.repository.delete(testSession);
        expectedResult = this.repository.saveAll(expectedResult);

        List<Session> sessions = sessionService.getAllSessions();

        assertEquals(expectedResult.size(), sessions.size());
        assertTrue(sessions.containsAll(expectedResult));
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

    @Test
    @DisplayName("Deleting session deletes session")
    void deleteSession_DeletesSession() throws NotFoundException {
        sessionService.deleteSession(testSession.getId());
        assertEquals(Collections.emptyList(), repository.findAll());
    }

    @Test
    @DisplayName("Deleting session does not throw")
    void deleteSession_DoesNotThrow() {
        assertDoesNotThrow(() -> sessionService.deleteSession(testSession.getId()));
    }

    @Test
    @DisplayName("Request Not existing session to be planned throws not found")
    void requestNotExistingSessionToBePlanned_ThrowsNotFound() throws NotFoundException {
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
        sessionService.requestSessionToBePlanned(testSession.getId());

        assertEquals(SessionState.TO_BE_PLANNED, repository.findById(testSession.getId()).get().getState());
    }
}