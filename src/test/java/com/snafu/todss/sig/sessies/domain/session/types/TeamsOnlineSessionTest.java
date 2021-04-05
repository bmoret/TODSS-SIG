package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TeamsOnlineSessionTest {
    private static TeamsOnlineSession session;

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String joinUrl = "https://website.com/join";
        session = new TeamsOnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                joinUrl
        );
    }

    @AfterEach
    void tearDown() {
        session = null;
    }

    @ParameterizedTest
    @EnumSource(SessionState.class)
    @DisplayName("Testing constructor of online session, the created instance returns all initial constructed parameters through getters")
    void onlineSessionConstructor_CreatesInstance(SessionState state) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String joinUrl = "https://website.com/join";

        session = new TeamsOnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                state,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                joinUrl
        );

        SessionDetails details = session.getDetails();
        assertEquals(now, details.getStartDate());
        assertEquals(nowPlusOneHour, details.getEndDate());
        assertEquals(subject, details.getSubject());
        assertEquals(description, details.getDescription());
        assertEquals(state, session.getState());
        assertEquals("Teams", session.getPlatform());
        assertEquals(joinUrl, session.getJoinURL());
    }

    @ParameterizedTest
    @MethodSource("provideSessionStates")
    @DisplayName("Go to next session state and expect session state")
    void goToNextSession(SessionState state, SessionState expectedNextState) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String joinUrl = "https://website.com/join";
        session = new TeamsOnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                state,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                joinUrl
        );

        session.nextState();

        assertEquals(expectedNextState, session.getState());
    }
    static Stream<Arguments> provideSessionStates() {
        return Stream.of(
                Arguments.of(SessionState.DRAFT, SessionState.TO_BE_PLANNED),
                Arguments.of(SessionState.TO_BE_PLANNED, SessionState.PLANNED),
                Arguments.of(SessionState.PLANNED, SessionState.ONGOING),
                Arguments.of(SessionState.ONGOING, SessionState.ENDED),
                Arguments.of(SessionState.ENDED, SessionState.ENDED)
        );
    }

    @Test
    @DisplayName("When changing platform throw UnsupportedOperationException")
    void whenChangingPlatform_ThrowsException() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> session.setPlatform("SomeOtherPlatform")
        );
    }
}