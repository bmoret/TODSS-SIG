package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.OnlineSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SessionDirectorTest {
    private static Person person = mock(Person.class);

    @Test
    @DisplayName("When input request not of specified class throw exception")
    void whenRequestNotOfSubClass_ThrowIllegalArgumentException() {
        SessionRequest request = new PhysicalSessionRequest();
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionDirector.build(request, null, null)
        );
    }

    @Test
    @DisplayName("When input request not of specified class throw exception")
    void whenRequestIsNull_ThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionDirector.build(null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionArgs")
    @DisplayName("Build sessions from input and create instances of correct sub class")
    void buildSessions_CreatesInstances(SessionRequest request, Class<Session> expectedClass) {
        Session session = SessionDirector.build(request, null, null);

        assertTrue(expectedClass.isInstance(session));
    }
    static Stream<Arguments> provideSessionArgs() {
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
        OnlineSessionRequest teamsOnlineSessionRequest = new OnlineSessionRequest(
                now,
                nowPlusOneHour,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Teams",
                "link",
                UUID.randomUUID().toString()
        );
        return Stream.of(
                Arguments.of(physicalSessionRequest, PhysicalSession.class),
                Arguments.of(onlineSessionRequest, OnlineSession.class),
                Arguments.of(teamsOnlineSessionRequest, TeamsOnlineSession.class)
        );
    }

    @Test
    @DisplayName("Build session from mocked SessionRequest throws IllegalArgumentException")
    void buildSessionsFromMockedSessionRequest_ThrowsIAE() {
        SessionRequest request = mock(SessionRequest.class);
        request.subject = "subject";
        request.description = "description";
        request.startDate = LocalDateTime.now();
        request.endDate = LocalDateTime.now().plusHours(1);
        request.sigId = UUID.randomUUID();
        request.contactPerson = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> SessionDirector.build(request, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionArgs")
    @DisplayName("Update sessions from input and updated instances has correct new value")
    void updateSessions_UpdatedInstanceHasUpdatedValue(SessionRequest request, Class<Session> expectedClass) {
        Session session = SessionDirector.build(request, null, null);
        String newSubject = "New Subject name";
        request.subject = newSubject;

        session = SessionDirector.update(session, request, null, null);

        assertEquals(newSubject, session.getDetails().getSubject());
    }

    @ParameterizedTest
    @MethodSource("provideSessionArgs")
    @DisplayName("Update sessions from input and updated instances is still the correct sub class")
    void updateSessions_UpdatedInstanceOfSameSubClass(SessionRequest request, Class<Session> expectedClass) {
        Session session = SessionDirector.build(request, null, null);
        request.subject = "New Subject name";

        session = SessionDirector.update(session, request, null, null);

        assertTrue(expectedClass.isInstance(session));
    }

    @Test
    @DisplayName("Update session from mocked SessionRequest throws IllegalArgumentException")
    void updateSessionsFromMockedSessionRequest_ThrowsIAE() {
        SessionRequest request = mock(SessionRequest.class);
        request.subject = "subject";
        request.description = "description";
        request.startDate = LocalDateTime.now();
        request.endDate = LocalDateTime.now().plusHours(1);
        request.sigId = UUID.randomUUID();
        request.contactPerson = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> SessionDirector.update( new PhysicalSession(), request, null, null)
        );
    }
}