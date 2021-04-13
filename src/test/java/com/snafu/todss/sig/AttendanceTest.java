package com.snafu.todss.sig;

import com.snafu.todss.sig.sessies.domain.Attendance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Attendance tests")
public class AttendanceTest {
    private Attendance attendance;

    @BeforeEach
    void createUseableAttendance() {
        attendance = new Attendance();
    }

    private static Stream<Arguments> attendanceFillExamples() {
        return Stream.of(
                Arguments.of(true,true,true),
                Arguments.of(true,true,false),
                Arguments.of(true,false,true),
                Arguments.of(true,false,false),
                Arguments.of(false,true,true),
                Arguments.of(false,true,false),
                Arguments.of(false,false,true),
                Arguments.of(false,false,false)
        );
    }

    @ParameterizedTest
    @MethodSource("attendanceFillExamples")
    @DisplayName("create all possible attendances")
    void createAttendance(boolean isConfirmed, boolean isAbsent, boolean isSpeaker) {
        Attendance constAttendance = new Attendance(isConfirmed, isAbsent, isSpeaker, null, null);
    }

    @Test
    @DisplayName("isConfirmed test")
    void isConfirmed() {
        assertFalse(attendance.isConfirmed());
        attendance.setConfirmed(true);
        assertTrue(attendance.isConfirmed());
    }

    @Test
    @DisplayName("isAbsence test")
    void isAbsence() {
        assertFalse(attendance.isAbsent());
        attendance.setAbsent(true);
        assertTrue(attendance.isAbsent());
    }

    @Test
    @DisplayName("isSpeaker test")
    void isSpeaker() {
        assertFalse(attendance.isSpeaker());
        attendance.setSpeaker(true);
        assertTrue(attendance.isSpeaker());
    }
}
