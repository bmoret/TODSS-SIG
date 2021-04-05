package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.OnlineSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SessionDirectorTest {
    @Test
    @DisplayName("When input request not of specified class throw exception")
    void whenRequestNotOfSubClass_ThrowIllegalArgumentException() {
        SessionRequest request = new SessionRequest();
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionDirector.build(request, null)
        );
    }

    @Test
    @DisplayName("When input request not of specified class throw exception")
    void whenRequestIsNull_ThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionDirector.build(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionArgs")
    @DisplayName("Build sessions from input and create instances of correct sub class")
    void buildSessions_CreatesInstances(SessionRequest request, Class<Session> expectedClass) {
        Session session = SessionDirector.build(request, null);

        assertTrue(expectedClass.isInstance(session));
    }
    static Stream<Arguments> provideSessionArgs() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);

        PhysicalSessionRequest physicalSessionRequest = new PhysicalSessionRequest();
        physicalSessionRequest.startDate = now;
        physicalSessionRequest.endDate = nowPlusOneHour;

        OnlineSessionRequest onlineSessionRequest = new OnlineSessionRequest();
        onlineSessionRequest.platform = "Random Platform";
        onlineSessionRequest.startDate = now;
        onlineSessionRequest.endDate = nowPlusOneHour;

        OnlineSessionRequest teamsOnlineSessionRequest = new OnlineSessionRequest();
        teamsOnlineSessionRequest.platform = "Teams";
        teamsOnlineSessionRequest.startDate = now;
        teamsOnlineSessionRequest.endDate = nowPlusOneHour;
        return Stream.of(
                Arguments.of(physicalSessionRequest, PhysicalSession.class),
                Arguments.of(onlineSessionRequest, OnlineSession.class),
                Arguments.of(teamsOnlineSessionRequest, TeamsOnlineSession.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionArgs")
    @DisplayName("Update sessions from input and updated instances has correct new value")
    void updateSessions_UpdatedInstanceHasUpdatedValue(SessionRequest request, Class<Session> expectedClass) {
        Session session = SessionDirector.build(request, null);
        String newSubject = "New Subject name";
        request.subject = newSubject;

        session = SessionDirector.update(session, request, null);

        assertEquals(newSubject, session.getDetails().getSubject());
    }

    @ParameterizedTest
    @MethodSource("provideSessionArgs")
    @DisplayName("Update sessions from input and updated instances is still the correct sub class")
    void updateSessions_UpdatedInstanceOfSameSubClass(SessionRequest request, Class<Session> expectedClass) {
        Session session = SessionDirector.build(request, null);
        request.subject = "New Subject name";

        session = SessionDirector.update(session, request, null);

        assertTrue(expectedClass.isInstance(session));
    }
}