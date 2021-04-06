package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.parameters.P;

import java.util.stream.Stream;

import static com.snafu.todss.sig.sessies.domain.StateAttendance.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Attendance tests")
class AttendanceTest {
    private Attendance attendance;

    @BeforeEach
    void createUseableAttendance() {
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
    void createAttendance(StateAttendance state, boolean isSpeaker) {
        Attendance constAttendance = new Attendance(state, isSpeaker, null, null);
        Attendance attendanceOf = Attendance.of(state, isSpeaker, null, null);
        assertEquals(constAttendance.getState(), attendanceOf.getState());
        assertEquals(constAttendance.isSpeaker(), attendanceOf.isSpeaker());
        assertEquals(constAttendance.toString(), attendanceOf.toString());
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
        Session session = new Session();
        assertNull(attendance.getSession());
        attendance.setSession(session);
        assertNotNull(attendance.getSession());
    }


    @Test
    @DisplayName("toString test")
    void attendanceString() {
        assertEquals(attendance.toString(), new Attendance().toString());
    }
}
