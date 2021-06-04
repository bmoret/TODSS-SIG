package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceSpeakerRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceStateRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.snafu.todss.sig.sessies.domain.AttendanceState.CANCELED;
import static com.snafu.todss.sig.sessies.domain.AttendanceState.PRESENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class AttendanceServiceTest {
    private static final SpringAttendanceRepository ATTENDANCE_REPOSITORY = mock(SpringAttendanceRepository.class);
    private static final PersonService PERSON_SERVICE = mock(PersonService.class);
    private static final SessionService SESSION_SERVICE = mock(SessionService.class);
    private AttendanceService SERVICE = new AttendanceService(ATTENDANCE_REPOSITORY, PERSON_SERVICE, SESSION_SERVICE);

    private static Attendance attendance;
    private static Person person;
    private static Session session;

    @BeforeEach
    @DisplayName("Create mocks for services")
    void setup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = new SpecialInterestGroup();
        session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );

        SessionRequest request = new PhysicalSessionRequest();

        person = new Person();
        attendance = new Attendance(CANCELED, true, person, session);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(ATTENDANCE_REPOSITORY, PERSON_SERVICE, SESSION_SERVICE);
    }


    private AttendanceService MOCKSERVICE = mock(AttendanceService.class);
    @Test
    @DisplayName("get attendance by id")
    void getAttendance1() throws NotFoundException {
        UUID uuid = UUID.randomUUID();

        when(MOCKSERVICE.getAttendanceById(uuid)).thenReturn(attendance);

        Attendance serviceAttendance = MOCKSERVICE.getAttendanceById(uuid);

        assertEquals(attendance, serviceAttendance);
        verify(MOCKSERVICE, times(1)).getAttendanceById(uuid);
    }

    @Test
    @DisplayName("get attendance by session")
    void getAttendanceBySession() throws NotFoundException {
        when(MOCKSERVICE.getAllAttendeesFromSession(any())).thenReturn(List.of(attendance));

        List<Attendance> attendances = MOCKSERVICE.getAllAttendeesFromSession(session.getId());
        assertEquals(List.of(attendance), attendances);
    }

    @Test
    @DisplayName("get attendance by id")
    void getAttendance() throws NotFoundException {
        UUID uuid = UUID.randomUUID();

        when(ATTENDANCE_REPOSITORY.findById(uuid)).thenReturn(Optional.of(attendance));

        Attendance serviceAttendance = SERVICE.getAttendanceById(uuid);

        assertEquals(attendance, serviceAttendance);
        verify(ATTENDANCE_REPOSITORY, times(1)).findById(uuid);
    }

    @Test
    @DisplayName("get attendance by id throws exception")
    void getAttendanceThrows() {
        UUID uuid = UUID.randomUUID();

        when(ATTENDANCE_REPOSITORY.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.getAttendanceById(uuid)
        );

        verify(ATTENDANCE_REPOSITORY, times(1)).findById(uuid);
    }

    @Test
    @DisplayName("create attendance")
    void createAttendance() throws Exception {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(SESSION_SERVICE.getSessionById(sessionId))
                .thenReturn(session);
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class)))
                .thenReturn(new Attendance(PRESENT, true, person, session));

        Attendance createdAttendance = SERVICE.createAttendance(PRESENT, true, personId, sessionId);

        assertSame(PRESENT, createdAttendance.getState());
        assertTrue(createdAttendance.isSpeaker());
        verify(PERSON_SERVICE, times(1)).getPerson(any());
        verify(SESSION_SERVICE, times(1)).getSessionById(any());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("create attendance throws exception when person and session are missing")
    void throwExceptionWhenMissingBoth() throws NotFoundException {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(PERSON_SERVICE.getPerson(personId)).thenThrow(NotFoundException.class);
        when(SESSION_SERVICE.getSessionById(sessionId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.createAttendance(PRESENT, true, personId, sessionId)
        );

        verify(PERSON_SERVICE, times(1)).getPerson(personId);
        verify(SESSION_SERVICE, times(0)).getSessionById(sessionId);
    }

    @Test
    @DisplayName("create attendance throws exception when person is missing")
    void throwExceptionWhenMissingPerson() throws NotFoundException {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(PERSON_SERVICE.getPerson(personId)).thenThrow(NotFoundException.class);
        when(SESSION_SERVICE.getSessionById(sessionId)).thenReturn(session);

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.createAttendance(PRESENT, true, personId, sessionId)
        );

        verify(PERSON_SERVICE, times(1)).getPerson(personId);
        verify(SESSION_SERVICE, times(0)).getSessionById(sessionId);
    }

    @Test
    @DisplayName("create attendance throws exception when session is missing")
    void throwExceptionWhenMissingSession() throws NotFoundException {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(PERSON_SERVICE.getPerson(personId)).thenReturn(person);
        when(SESSION_SERVICE.getSessionById(sessionId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.createAttendance(PRESENT, true, personId, sessionId)
        );

        verify(PERSON_SERVICE, times(1)).getPerson(any());
        verify(SESSION_SERVICE, times(1)).getSessionById(sessionId);
    }

    @Test
    @DisplayName("update speaker of attendance")
    void updateSpeakerAttendance() throws Exception {
        AttendanceSpeakerRequest request = new AttendanceSpeakerRequest();
        request.speaker = false;
        Attendance updatedAttendance = new Attendance(PRESENT, false, person, session);

        when(ATTENDANCE_REPOSITORY.findById(any())).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class))).thenReturn(updatedAttendance);

        Attendance actualUpdatedAttendance = SERVICE.updateSpeakerAttendance(UUID.randomUUID(), request);

        assertFalse(actualUpdatedAttendance.isSpeaker());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
        verify(ATTENDANCE_REPOSITORY, times(1)).findById(any());
    }

    @Test
    @DisplayName("throw exception when cant find attendance by id in updateAttendance")
    void ThrowExceptionWhenNoPersonInUpdateSpeaker() {
        UUID attendanceId = UUID.randomUUID();
        AttendanceSpeakerRequest request = new AttendanceSpeakerRequest();
        request.speaker = false;

        when(ATTENDANCE_REPOSITORY.findById(attendanceId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.updateSpeakerAttendance(attendanceId, request)
        );

        verify(ATTENDANCE_REPOSITORY, times(1)).findById(attendanceId);
    }

    @Test
    @DisplayName("update state of attendance")
    void updateAttendance() throws Exception {
        AttendanceStateRequest request = new AttendanceStateRequest();
        request.state = PRESENT;
        Attendance updatedAttendance = new Attendance(PRESENT, false, person, session);

        when(ATTENDANCE_REPOSITORY.findById(any())).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class))).thenReturn(updatedAttendance);

        Attendance actualUpdatedAttendance = SERVICE.updateStateAttendance(UUID.randomUUID(), request);

        assertEquals(PRESENT, actualUpdatedAttendance.getState());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
        verify(ATTENDANCE_REPOSITORY, times(1)).findById(any());
    }

    @Test
    @DisplayName("throw exception when cant find attendance by id in updateAttendance")
    void ThrowExceptionWhenNoPersonInUpdate() {
        UUID attendanceId = UUID.randomUUID();
        AttendanceStateRequest request = new AttendanceStateRequest();
        request.state = PRESENT;

        when(ATTENDANCE_REPOSITORY.findById(attendanceId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.updateStateAttendance(attendanceId, request)
        );

        verify(ATTENDANCE_REPOSITORY, times(1)).findById(attendanceId);
    }

    @Test
    @DisplayName("deleteAttendance deletes attendance")
    void deleteAttendance() {
        SERVICE.deleteAttendance(UUID.randomUUID());

        verify(ATTENDANCE_REPOSITORY, times(1)).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("get speakers from session")
    void getSpeakersFromAttendanceSession() throws NotFoundException {
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(attendance);
        when(SESSION_SERVICE.getSessionById(session.getId())).thenReturn(session);
        when(ATTENDANCE_REPOSITORY.findAttendancesBySession(session)).thenReturn(attendances);
        assertDoesNotThrow(
                () -> SERVICE.getSpeakersFromAttendanceSession(attendance.getSession().getId())
        );

        verify(SESSION_SERVICE, times(1)).getSessionById(session.getId());
        verify(ATTENDANCE_REPOSITORY, times(1)).findAttendancesBySession(session);

    }
}
