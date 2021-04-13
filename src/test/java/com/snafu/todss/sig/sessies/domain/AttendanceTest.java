package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.builder.SessionDirector;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                        address
                );

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
