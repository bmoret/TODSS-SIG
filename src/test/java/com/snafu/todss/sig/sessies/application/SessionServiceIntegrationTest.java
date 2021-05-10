package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
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
    private SessionService sessionService;

    @Autowired
    private SessionRepository repository;

    @Autowired
    private SpecialInterestGroupRepository sigRepository;

    private Session testSession;

    @BeforeEach
    void setup() {
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
                        address
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
                                "Address"
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
                                "link"
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
                                "link"
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
                "Address"
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

        session = sessionService.updateSession(session.getId(), request);

        assertEquals(request.subject, session.getDetails().getSubject());
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
                "Address"
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
                        "Address"
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
                        "Address"
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