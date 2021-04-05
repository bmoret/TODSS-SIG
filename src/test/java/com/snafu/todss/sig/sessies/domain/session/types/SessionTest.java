package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
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
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionTest {
    private static Session session;
    private static Person testPerson;
    private static Attendance testAttendance;
    private static Feedback testFeedback;

    @BeforeEach
    void setup() {
        session = mock(Session.class,
                Mockito.withSettings()
                        .useConstructor(
                                new SessionDetails(),
                                SessionState.DRAFT,
                                new SpecialInterestGroup(),
                                new ArrayList<>(),
                                new ArrayList<>()

                        )
                        .defaultAnswer(CALLS_REAL_METHODS));
        doCallRealMethod().when(session).addAttendee(any());
        doCallRealMethod().when(session).addFeedback(any());

        testPerson = new Person(
                new PersonDetails("mail", "first", "last", "expertise", LocalDate.now(), Branch.VIANEN, Role.EMPLOYEE),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        testAttendance = new Attendance(false, false, false, testPerson, session);

        testFeedback = new Feedback(
                "Description with feedback",
                session,
                testPerson
        );
    }

    @AfterEach
    void tearDown() {
        session = null;
    }

    @ParameterizedTest
    @EnumSource(SessionState.class)
    @DisplayName("Testing constructor of session")
    void sessionConstructor_CreatesInstance(SessionState state) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";

        session = mock(Session.class,
                Mockito.withSettings()
                        .useConstructor(
                                new SessionDetails(now, nowPlusOneHour, subject, description),
                                state,
                                new SpecialInterestGroup(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                        .defaultAnswer(CALLS_REAL_METHODS)
        );

        SessionDetails details = session.getDetails();
        assertEquals(now, details.getStartDate());
        assertEquals(nowPlusOneHour, details.getEndDate());
        assertEquals(subject, details.getSubject());
        assertEquals(description, details.getDescription());
        assertEquals(state, session.getState());
    }

    @Test
    @DisplayName("Add attendance session does not throw")
    void addAttendee_DoesNotThrow() {
        assertDoesNotThrow(
                () -> session.addAttendee(testAttendance)
        );
    }

    @Test
    @DisplayName("Add person to attend session is stored in session's attendances")
    void addAttendee_AddsAttendanceForPerson() {
        session.addAttendee(testAttendance);

        assertEquals(testPerson, session.getAttendances().stream().findFirst().get().getPerson());
    }

    @Test
    @DisplayName("Throws when a person is added twice")
    void addAttendeeTwice_ThrowsException() {
        session.addAttendee(testAttendance);

        assertThrows(
                IllegalArgumentException.class,
                () -> session.addAttendee(testAttendance)
        );
    }

    @Test
    @DisplayName("Throws when null is added as attendee")
    void addNullAsAttendee_ThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> session.addAttendee(null)
        );
    }

    @Test
    @DisplayName("Altering attendances from getter doesn't alter the list in Session itself")
    void alterAttendancesOfGetter_DoesNotAlterListInSession() {
        List<Attendance> attendanceList = session.getAttendances();
        Attendance attendance = new Attendance(false, false, false, testPerson, session);

        assertThrows(
                UnsupportedOperationException.class,
                () -> attendanceList.add(attendance)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRemoveAttendeeArgs")
    @DisplayName("Removing person's attendance should return boolean")
    void removePersonFromAttendances_ShouldReturnBoolean(Attendance addedAttendance, Person toBeRemovedAttendance, boolean shouldReturn) {
        session.addAttendee(addedAttendance);

        assertEquals(shouldReturn, session.removeAttendee(toBeRemovedAttendance));
    }
    static Stream<Arguments> provideRemoveAttendeeArgs() {
        testPerson = new Person(
                new PersonDetails("mail", "first", "last", "expertise", LocalDate.now(), Branch.VIANEN, Role.EMPLOYEE),
                null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        testAttendance = new Attendance(false, false, false, testPerson, session);
        return Stream.of(
                Arguments.of(
                        testAttendance,
                        testPerson,
                        true
                ),
                Arguments.of(
                        testAttendance,
                        null,
                        false
                ),
                Arguments.of(
                        testAttendance,
                        new Person(),
                        false
                ),
                Arguments.of(
                        new Attendance(false, false, false, new Person(), session),
                        testPerson,
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideRemoveAttendeeArgs")
    @DisplayName("Remove a person from attendances removes attendance of person")
    void removePersonFromAttendances_RemovesAttendance(Attendance addedAttendance, Person toBeRemovedAttendance, boolean shouldReturn) {
        session.addAttendee(addedAttendance);

        session.removeAttendee(toBeRemovedAttendance);

        assertEquals(shouldReturn, session.getAttendances().isEmpty());
    }

    @Test
    @DisplayName("Add feedback to session does not throw")
    void addFeedback_DoesNotThrow() {
        assertDoesNotThrow(
                () -> session.addFeedback(testFeedback)
        );
    }

    @Test
    @DisplayName("Add feedback to session is stored in sessions feedback")
    void addFeedback_AddsFeedback() {
        session.addFeedback(testFeedback);

        assertEquals(testFeedback, session.getFeedback().stream().findFirst().get());
    }

    @Test
    @DisplayName("Throws when null is added as feedback")
    void addNullAsFeedback_ThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> session.addFeedback(null)
        );
    }

    @Test
    @DisplayName("Altering feedback list from getter doesn't alter the list in Session itself")
    void alterFeedbackListOfGetter_DoesNotAlterListInSession() {
        List<Feedback> feedbackList = session.getFeedback();

        assertThrows(
                UnsupportedOperationException.class,
                () -> feedbackList.add(testFeedback)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRemoveFeedbackArgs")
    @DisplayName("Remove a feedback from feedback list removes the feedback")
    void removeFeedback_ShouldReturnBoolean(Feedback addedFeedback, Feedback toBeRemovedFeedback, boolean shouldReturn) {
        session.addFeedback(addedFeedback);

        assertEquals(shouldReturn, session.removeFeedback(toBeRemovedFeedback));
    }
    static Stream<Arguments> provideRemoveFeedbackArgs() {
        testPerson = new Person(
                new PersonDetails("mail", "first", "last", "expertise", LocalDate.now(), Branch.VIANEN, Role.EMPLOYEE),
                null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        session = mock(Session.class,
                Mockito.withSettings()
                        .useConstructor(
                                new SessionDetails(),
                                SessionState.DRAFT,
                                new SpecialInterestGroup(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                        .defaultAnswer(CALLS_REAL_METHODS)
        );

        testFeedback = new Feedback("Some feedback", session, testPerson);
        return Stream.of(
                Arguments.of(testFeedback, testFeedback, true),
                Arguments.of(testFeedback, null, false),
                Arguments.of(testFeedback, new Feedback(), false),
                Arguments.of(new Feedback(), testFeedback, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRemoveFeedbackArgs")
    @DisplayName("Remove a feedback removes the feedback")
    void removeFeedback_RemovesFeedback(Feedback addedFeedback, Feedback toBeRemovedFeedback, boolean shouldReturn) {
        session.addFeedback(addedFeedback);

        session.removeFeedback(toBeRemovedFeedback);

        assertEquals(shouldReturn, session.getFeedback().isEmpty());
    }



    @ParameterizedTest
    @MethodSource("provideSessionStates")
    @DisplayName("Go to next session state and expect session state")
    void goToNextSession(SessionState state, SessionState expectedNextState) {
        session = mock(Session.class,
                Mockito.withSettings()
                        .useConstructor(
                                new SessionDetails(),
                                state,
                                new SpecialInterestGroup(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                        .defaultAnswer(CALLS_REAL_METHODS)
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
}