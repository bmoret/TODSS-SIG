package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.AttendanceState;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
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
import java.util.ArrayList;
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
                () -> SessionDirector.update(new PhysicalSession(), request, null, null)
        );
    }

    private PhysicalSessionRequest providePhysicalSessionRequest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);

        return new PhysicalSessionRequest(
                now,
                nowPlusOneHour,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Address",
                UUID.randomUUID().toString()
        );
    }

    private OnlineSessionRequest provideOnlineSessionRequest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);

        return new OnlineSessionRequest(
                now,
                nowPlusOneHour,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Random Platform",
                "link",
                UUID.randomUUID().toString()
        );
    }

    @Test
    @DisplayName("change session type from Physical to Online")
    void changeSessionFromPhysicalToOnline() {
        Session session = SessionDirector.build(providePhysicalSessionRequest(), null, null);
        session.addAttendee(new Attendance(AttendanceState.PRESENT, false, person, session));
        session.addFeedback(new Feedback("leuk!@!@!@!@!@!", session, person));
        session.addFeedback(new Feedback("hallo daaarrrrrrrrrrrr", session, person));
        Session updatedSession = SessionDirector.update(session, provideOnlineSessionRequest(), null, null);

        assertEquals(session.getId(), updatedSession.getId());
        assertEquals(session.getAttendances().size(), updatedSession.getAttendances().size());
        assertEquals(session.getFeedback().size(), updatedSession.getFeedback().size());
    }

    @Test
    @DisplayName("change session type from Online to Physical")
    void changeSessionFromOnlineToPhysical() {
        Session session = SessionDirector.build(provideOnlineSessionRequest(), null, null);
        session.addAttendee(new Attendance(AttendanceState.PRESENT, false, person, session));
        session.addFeedback(new Feedback("leuk!@!@!@!@!@!", session, person));
        session.addFeedback(new Feedback("hallo daaarrrrrrrrrrrr", session, person));
        Session updatedSession = SessionDirector.update(session, providePhysicalSessionRequest(), null, null);
        for (Feedback feedback : updatedSession.getFeedback()) {
            System.out.println(feedback.getDescription());
        }
        assertEquals(session.getId(), updatedSession.getId());
        assertEquals(session.getAttendances().size(), updatedSession.getAttendances().size());
        assertEquals(session.getFeedback().size(), updatedSession.getFeedback().size());
    }

    @Test
    @DisplayName("change session type throws when no correct Request is found")
    void changeSessionThrows() {
        Session session = SessionDirector.build(provideOnlineSessionRequest(), null, null);
        session.addAttendee(new Attendance(AttendanceState.PRESENT, false, person, session));
        session.addFeedback(new Feedback("leuk!@!@!@!@!@!", session, person));
        session.addFeedback(new Feedback("hallo daaarrrrrrrrrrrr", session, person));
        assertThrows(
                IllegalArgumentException.class,
                () -> SessionDirector.rebuild(
                        session,
                        mock(SessionRequest.class),
                        null,
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionDateEditable")
    @DisplayName("Session state DRAFT & TO_BE_PLANNED can change date")
    void canChangeDateInState(Session session, SessionRequest request) {
        LocalDateTime startDate = session.getDetails().getStartDate();
        LocalDateTime endDate = session.getDetails().getEndDate();

        session = SessionDirector.update(session,
                request,
                null,
                null
        );

        assertNotEquals(startDate, session.getDetails().getStartDate());
        assertNotEquals(endDate, session.getDetails().getEndDate());
        assertEquals(request.startDate, session.getDetails().getStartDate());
        assertEquals(request.endDate, session.getDetails().getEndDate());
    }

    static Stream<Arguments> provideSessionDateEditable() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        LocalDateTime nowRequest = LocalDateTime.now().plusHours(2);
        LocalDateTime nowPlusOneHourRequest = LocalDateTime.now().plusHours(3);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";

        Session physicalSession = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );
        Session onlineSession = new OnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Platform",
                "join url",
                null
        );
        Session teamsOnlineSession = new TeamsOnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "join url",
                null
        );
        Session physicalSession2 = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );
        Session onlineSession2 = new OnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Platform",
                "join url",
                null
        );
        Session teamsOnlineSession2 = new TeamsOnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.TO_BE_PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "join url",
                null
        );
        PhysicalSessionRequest physicalSessionRequest = new PhysicalSessionRequest(
                nowRequest,
                nowPlusOneHourRequest,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Address",
                UUID.randomUUID().toString()
        );
        OnlineSessionRequest onlineSessionRequest = new OnlineSessionRequest(
                nowRequest,
                nowPlusOneHourRequest,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Random Platform",
                "link",
                UUID.randomUUID().toString()
        );
        OnlineSessionRequest teamsOnlineSessionRequest = new OnlineSessionRequest(
                nowRequest,
                nowPlusOneHourRequest,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Teams",
                "link",
                UUID.randomUUID().toString()
        );
        return Stream.of(
                Arguments.of(physicalSession, physicalSessionRequest),
                Arguments.of(onlineSession, onlineSessionRequest),
                Arguments.of(teamsOnlineSession, teamsOnlineSessionRequest),
                Arguments.of(physicalSession2, physicalSessionRequest),
                Arguments.of(onlineSession2, onlineSessionRequest),
                Arguments.of(teamsOnlineSession2, teamsOnlineSessionRequest)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionNotDateEditable")
    @DisplayName("Session state after To_BE_PLANNED cannot change date")
    void cannotChangeDateInState(Session session, SessionRequest request) {
        LocalDateTime startDate = session.getDetails().getStartDate();
        LocalDateTime endDate = session.getDetails().getEndDate();

        session = SessionDirector.update(session,
                request,
                null,
                null
        );


        assertEquals(startDate, session.getDetails().getStartDate());
        assertEquals(endDate, session.getDetails().getEndDate());
    }

    static Stream<Arguments> provideSessionNotDateEditable() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        LocalDateTime nowRequest = LocalDateTime.now().plusHours(2);
        LocalDateTime nowPlusOneHourRequest = LocalDateTime.now().plusHours(3);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";

        Session physicalSession = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );
        Session onlineSession = new OnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "Platform",
                "join url",
                null
        );
        Session teamsOnlineSession = new TeamsOnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.PLANNED,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                "join url",
                null
        );

        PhysicalSessionRequest physicalSessionRequest = new PhysicalSessionRequest(
                nowRequest,
                nowPlusOneHourRequest,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Address",
                UUID.randomUUID().toString()
        );
        OnlineSessionRequest onlineSessionRequest = new OnlineSessionRequest(
                nowRequest,
                nowPlusOneHourRequest,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Random Platform",
                "link",
                UUID.randomUUID().toString()
        );
        OnlineSessionRequest teamsOnlineSessionRequest = new OnlineSessionRequest(
                nowRequest,
                nowPlusOneHourRequest,
                "Subject",
                "Description",
                UUID.randomUUID(),
                "Teams",
                "link",
                UUID.randomUUID().toString()
        );
        return Stream.of(
                Arguments.of(physicalSession, physicalSessionRequest),
                Arguments.of(onlineSession, onlineSessionRequest),
                Arguments.of(teamsOnlineSession, teamsOnlineSessionRequest)
        );
    }

}