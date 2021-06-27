package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PhysicalSessionTest {
    private static PhysicalSession session;

    @AfterEach
    void tearDown() {
        session = null;
    }

    @ParameterizedTest
    @EnumSource(SessionState.class)
    @DisplayName("Testing constructor of physical session, the created instance returns all inital constructed parameters through getters")
    void physicalSessionConstructor_CreatesInstance(SessionState state) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";

        session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                state,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );

        SessionDetails details = session.getDetails();
        assertEquals(now, details.getStartDate());
        assertEquals(nowPlusOneHour, details.getEndDate());
        assertEquals(subject, details.getSubject());
        assertEquals(description, details.getDescription());
        assertEquals(state, session.getState());
        assertEquals(address, session.getAddress());
    }

    @ParameterizedTest
    @MethodSource("provideSessionStates")
    @DisplayName("Go to next session state and expect session state")
    void goToNextSession(SessionState state, SessionState expectedNextState) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                state,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
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

    @ParameterizedTest
    @MethodSource("provideSessionEquals")
    @DisplayName("Test Equals")
    void equalsTest(PhysicalSession session, PhysicalSession equalsSession, boolean isEquals) {
        assertEquals(isEquals, session.equals(equalsSession));
        if (equalsSession != null) {
            assertEquals(isEquals, session.hashCode() == equalsSession.hashCode());
        }
    }
    static Stream<Arguments> provideSessionEquals() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );
        return Stream.of(
                Arguments.of(session, session, true),
                Arguments.of(session, null, false),
                Arguments.of(session, new PhysicalSession(), false),
                Arguments.of(new PhysicalSession(), session, false)
        );
    }
}