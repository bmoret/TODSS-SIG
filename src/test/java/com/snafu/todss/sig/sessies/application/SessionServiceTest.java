package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
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

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {
    private static final SessionRepository repository = mock(SessionRepository.class);
    private static final SpecialInterestGroupService sigService = mock(SpecialInterestGroupService.class);
    private static SessionService service;
    private static Session session;
    private static PhysicalSessionRequest physicalSessionRequest;

    @BeforeAll
    static void init() {
        physicalSessionRequest = new PhysicalSessionRequest();
    }

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        service = new SessionService(repository, sigService);
        physicalSessionRequest.startDate = now;
        physicalSessionRequest.endDate = nowPlusOneHour;
        physicalSessionRequest.subject = subject;
        physicalSessionRequest.description = description;
        physicalSessionRequest.sigId = UUID.randomUUID();
        physicalSessionRequest.address = address;

        session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address
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
    @DisplayName("Delete session creates session")
    void deleteSession_DeletesSession(){
        service.deleteSession(UUID.randomUUID());

        verify(repository, times(1)).deleteById(any(UUID.class));
    }
}