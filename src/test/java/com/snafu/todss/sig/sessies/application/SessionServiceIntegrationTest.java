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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

    private Session session;

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        System.out.println("Sig saved is: " + sig.getId());
        this.session = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address
                )
        );
        System.out.println("Session saved is: " + session.getId());
    }

    @AfterEach
    void tearDown() {
        this.repository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideAllSessionsList")
    @DisplayName("Get all sessions")
    void getAllSessions_ReturnsCorrectSessions(List<Session> expectedResult) {
        this.repository.delete(session);
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

}