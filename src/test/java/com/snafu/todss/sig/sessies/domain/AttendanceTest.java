package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static com.snafu.todss.sig.sessies.domain.AttendanceState.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Attendance tests")
class AttendanceTest {
    private Attendance attendance;

    @BeforeEach
    void createUsableAttendance() {
        attendance = new Attendance();
    }

    private static Stream<Arguments> attendanceFillExamples() {
        return Stream.of(
                Arguments.of(NO_SHOW, true),
                Arguments.of(NO_SHOW, false),
                Arguments.of(CANCELED, true),
                Arguments.of(CANCELED, false),
                Arguments.of(PRESENT, true),
                Arguments.of(PRESENT, false)
        );
    }
    @ParameterizedTest
    @MethodSource("attendanceFillExamples")
    @DisplayName("create all possible attendances")
    void createAttendance(AttendanceState state, boolean isSpeaker) {
        Attendance constAttendance = new Attendance(state, isSpeaker, null, null);
        assertEquals(state, constAttendance.getState());
        assertEquals(isSpeaker, constAttendance.isSpeaker());
    }

    @Test
    @DisplayName("Attendance.of() returns new attendance with all false")
    void attendanceOf_ReturnsNewAttendance() {
        Person person = new Person();
        Session session = new PhysicalSession();
        Attendance attendance = Attendance.of(person, session);

        assertEquals(person, attendance.getPerson());
        assertEquals(session, attendance.getSession());
        assertEquals(NO_SHOW, attendance.getState());
        assertFalse(attendance.isSpeaker());
    }

    @Test
    @DisplayName("check if state only contains one state")
    void stateAttendance() {
        attendance.setState(NO_SHOW);
        assertSame(NO_SHOW, attendance.getState());
        assertNotSame(CANCELED, attendance.getState());
        assertNotSame(PRESENT, attendance.getState());

        attendance.setState(CANCELED);
        assertSame(CANCELED, attendance.getState());
        assertNotSame(NO_SHOW, attendance.getState());
        assertNotSame(PRESENT, attendance.getState());

        attendance.setState(PRESENT);
        assertSame(PRESENT, attendance.getState());
        assertNotSame(NO_SHOW, attendance.getState());
        assertNotSame(CANCELED, attendance.getState());
    }

    @Test
    @DisplayName("isSpeaker test")
    void isSpeaker() {
        assertFalse(attendance.isSpeaker());
        attendance.setSpeaker(true);
        assertTrue(attendance.isSpeaker());
    }

    @Test
    @DisplayName("get/set person test")
    void getSetPersonInAttendance() {
        Person person = new Person();
        assertNull(attendance.getPerson());
        attendance.setPerson(person);
        assertNotNull(attendance.getPerson());
    }

    @Test
    @DisplayName("get/set session test")
    void getSetSessionInAttendance() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = new SpecialInterestGroup();
        Session session = new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address,
                null
                );

        assertNull(attendance.getSession());
        attendance.setSession(session);
        assertNotNull(attendance.getSession());
    }
}
