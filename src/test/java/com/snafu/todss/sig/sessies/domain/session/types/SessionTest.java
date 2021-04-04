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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionTest {
    private static Session session;
    private static Person testAttendee;
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

        testAttendee = new Person(
                new PersonDetails("mail", "first", "last", "expertise", LocalDate.now(), Branch.VIANEN, Role.EMPLOYEE),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        testFeedback = new Feedback(
                "Description with feedback",
                session,
                testAttendee
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
        assertDoesNotThrow(
                () -> mock(Session.class,
                        Mockito.withSettings()
                                .useConstructor(
                                        new SessionDetails(),
                                        state,
                                        new SpecialInterestGroup(),
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                )
                                .defaultAnswer(CALLS_REAL_METHODS)
                )
        );
    }

    @Test
    @DisplayName("Add person to attend session does not throw")
    void addAttendee_DoesNotThrow() {
        assertDoesNotThrow(
                () -> session.addAttendee(testAttendee)
        );
    }

    @Test
    @DisplayName("Add person to attend session is stored in session's attendances")
    void addAttendee_AddsAttendanceForPerson() {
        session.addAttendee(testAttendee);

        assertEquals(testAttendee, session.getAttendances().stream().findFirst().get().getPerson());
    }

    @Test
    @DisplayName("Throws when a person is added twice")
    void addAttendeeTwice_ThrowsException() {
        session.addAttendee(testAttendee);

        assertThrows(
                IllegalArgumentException.class,
                () -> session.addAttendee(testAttendee)
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
        Attendance attendance = new Attendance(false, false, false, testAttendee, session);

        assertThrows(
                UnsupportedOperationException.class,
                () -> attendanceList.add(attendance)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRemoveAttendeeArgs")
    @DisplayName("test")
    void removePersonFromAttendances_ShouldReturnBoolean(Person addedAttendee, Person toBeRemovedAttendee, boolean shouldReturn) {
        session.addAttendee(addedAttendee);

        assertEquals(shouldReturn, session.removeAttendee(toBeRemovedAttendee));
    }
    static Stream<Arguments> provideRemoveAttendeeArgs() {
        testAttendee = new Person(
                new PersonDetails("mail", "first", "last", "expertise", LocalDate.now(), Branch.VIANEN, Role.EMPLOYEE),
                null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );
        return Stream.of(
                Arguments.of(testAttendee, testAttendee, true),
                Arguments.of(testAttendee, null, false),
                Arguments.of(testAttendee, new Person(), false),
                Arguments.of(new Person(), testAttendee, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRemoveAttendeeArgs")
    @DisplayName("Remove a person from attendances removes attendance of person")
    void removePersonFromAttendances_RemovesAttendance(Person addedAttendee, Person toBeRemovedAttendee, boolean shouldReturn) {
        session.addAttendee(addedAttendee);

        session.removeAttendee(toBeRemovedAttendee);

        assertEquals(
                shouldReturn,
                session.getAttendances().stream()
                        .noneMatch(attendance -> attendance.getPerson().equals(addedAttendee))
        );
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