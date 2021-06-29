package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.AttendanceState;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.PresenceRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.snafu.todss.sig.sessies.domain.AttendanceState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class AttendanceServiceTest {
    private static final SpringAttendanceRepository ATTENDANCE_REPOSITORY = mock(SpringAttendanceRepository.class);
    private static final PersonService PERSON_SERVICE = mock(PersonService.class);
    private static final SessionService SESSION_SERVICE = mock(SessionService.class);
    private final AttendanceService MOCKSERVICE = mock(AttendanceService.class);
    private final AttendanceService SERVICE =
            new AttendanceService(ATTENDANCE_REPOSITORY, PERSON_SERVICE, SESSION_SERVICE);

    private static Attendance attendance;
    private static Person person;
    private static Session session;
    private static SpecialInterestGroup sig;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
    private final String subject = "Subjectttt";
    private final String description = "Description";
    private final String address = "Address";

    @BeforeEach
    @DisplayName("Create mocks for services")
    void setup() {
        SpecialInterestGroup sig = new SpecialInterestGroup();
        session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.ENDED,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );
        person = new Person();
        attendance = new Attendance(CANCELED, true, person, session);
        session.addAttendee(attendance);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(ATTENDANCE_REPOSITORY, PERSON_SERVICE, SESSION_SERVICE);
    }

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
        verify(PERSON_SERVICE, times(1)).getPersonById(any());
        verify(SESSION_SERVICE, times(1)).getSessionById(any());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("create attendance throws exception when person and session are missing")
    void throwExceptionWhenMissingBoth() throws NotFoundException {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(PERSON_SERVICE.getPersonById(personId)).thenThrow(NotFoundException.class);
        when(SESSION_SERVICE.getSessionById(sessionId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.createAttendance(PRESENT, true, sessionId, personId)
        );

        verify(PERSON_SERVICE, times(1)).getPersonById(personId);
        verify(SESSION_SERVICE, times(0)).getSessionById(sessionId);
    }

    @Test
    @DisplayName("create attendance throws exception when person is missing")
    void throwExceptionWhenMissingPerson() throws NotFoundException {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(PERSON_SERVICE.getPersonById(personId)).thenThrow(NotFoundException.class);
        when(SESSION_SERVICE.getSessionById(sessionId)).thenReturn(session);

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.createAttendance(PRESENT, true, sessionId, personId)
        );

        verify(PERSON_SERVICE, times(1)).getPersonById(personId);
        verify(SESSION_SERVICE, times(0)).getSessionById(sessionId);
    }

    @Test
    @DisplayName("create attendance throws exception when session is missing")
    void throwExceptionWhenMissingSession() throws NotFoundException {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(PERSON_SERVICE.getPersonById(personId)).thenReturn(person);
        when(SESSION_SERVICE.getSessionById(sessionId)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.createAttendance(PRESENT, true, sessionId, personId)
        );

        verify(PERSON_SERVICE, times(1)).getPersonById(any());
        verify(SESSION_SERVICE, times(1)).getSessionById(sessionId);
    }

    @Test
    @DisplayName("update speaker of attendance")
    void updateSpeakerAttendance() throws Exception {
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();
        request.speaker = false;
        Attendance updatedAttendance = new Attendance(PRESENT, false, person, session);

        when(ATTENDANCE_REPOSITORY.findById(any())).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class))).thenReturn(updatedAttendance);

        Attendance actualUpdatedAttendance = SERVICE.updateAttendance(UUID.randomUUID(), request);

        assertFalse(actualUpdatedAttendance.isSpeaker());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
        verify(ATTENDANCE_REPOSITORY, times(1)).findById(any());
    }

    @Test
    @DisplayName("throw exception when cant find attendance by id in updateAttendance")
    void ThrowExceptionWhenNoPersonInUpdateSpeaker() {
        UUID attendanceId = UUID.randomUUID();
        AttendanceRequest request = new AttendanceRequest();
        request.speaker = false;

        when(ATTENDANCE_REPOSITORY.findById(attendanceId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.updateAttendance(attendanceId, request)
        );

        verify(ATTENDANCE_REPOSITORY, times(1)).findById(attendanceId);
    }

    @Test
    @DisplayName("update state of attendance")
    void updateAttendance() throws Exception {
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();
        Attendance updatedAttendance = new Attendance(PRESENT, false, person, session);

        when(ATTENDANCE_REPOSITORY.findById(any())).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class))).thenReturn(updatedAttendance);

        Attendance actualUpdatedAttendance = SERVICE.updateAttendance(UUID.randomUUID(), request);

        assertEquals(PRESENT, actualUpdatedAttendance.getState());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
        verify(ATTENDANCE_REPOSITORY, times(1)).findById(any());
    }

    @Test
    @DisplayName("update presence of attendance when attendee does not show up")
    void updatePresenceToNoSHow() throws Exception {
        PresenceRequest request = new PresenceRequest();
        request.isPresent = false;
        Attendance updatedAttendance = new Attendance(NO_SHOW, false, person, session);

        when(ATTENDANCE_REPOSITORY.findById(any())).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class))).thenReturn(updatedAttendance);

        Attendance actualUpdatedAttendance = SERVICE.updatePresence(UUID.randomUUID(), request);

        assertEquals(NO_SHOW, actualUpdatedAttendance.getState());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
        verify(ATTENDANCE_REPOSITORY, times(1)).findById(any());
    }

    @Test
    @DisplayName("update presence of attendance when has not begun")
    void updatePresenceWhenSessionNotBegun() {
        Session session1 = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );
        Attendance attendance1 = new Attendance(CANCELED, true, person, session1);
        session1.addAttendee(attendance1);
        PresenceRequest request = new PresenceRequest();
        request.isPresent = false;
        Attendance updatedAttendance = new Attendance(NO_SHOW, false, person, session1);

        when(ATTENDANCE_REPOSITORY.findById(any())).thenReturn(Optional.of(attendance1));
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class))).thenReturn(updatedAttendance);

        assertThrows(IllegalArgumentException.class, () -> SERVICE.updatePresence(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("update presence of attendance when attendee does shows up")
    void updatePresenceToPresent() {
        PresenceRequest request = new PresenceRequest();
        request.isPresent = true;
        Attendance updatedAttendance = new Attendance(PRESENT, false, person, session);

        when(ATTENDANCE_REPOSITORY.findById(any())).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.save(any(Attendance.class))).thenReturn(updatedAttendance);

        Attendance actualUpdatedAttendance = assertDoesNotThrow(
                () ->SERVICE.updatePresence(UUID.randomUUID(), request)
        );

        assertEquals(PRESENT, actualUpdatedAttendance.getState());
        verify(ATTENDANCE_REPOSITORY, times(1)).save(any(Attendance.class));
        verify(ATTENDANCE_REPOSITORY, times(1)).findById(any());
    }

    @Test
    @DisplayName("throw exception when cant find attendance by id in updateAttendance")
    void ThrowExceptionWhenNoPersonInUpdate() {
        UUID attendanceId = UUID.randomUUID();
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();

        when(ATTENDANCE_REPOSITORY.findById(attendanceId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> SERVICE.updateAttendance(attendanceId, request)
        );

        verify(ATTENDANCE_REPOSITORY, times(1)).findById(attendanceId);
    }

    @Test
    @DisplayName("deleteAttendance deletes attendance")
    void deleteAttendance() throws NotFoundException {
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

    @Test
    @DisplayName("check if attendance exists by looking for attendance containing session and person")
    void checkAttendanceBySessionAndPerson() {
        when(ATTENDANCE_REPOSITORY.findAttendanceByIdContainingAndSessionAndPerson(session, person))
                .thenReturn(Optional.of(attendance));

        assertDoesNotThrow(
                () -> SERVICE.getAttendanceBySessionAndPerson(session, person)
        );

        verify(ATTENDANCE_REPOSITORY, times(1))
                .findAttendanceByIdContainingAndSessionAndPerson(any(Session.class), any(Person.class));
    }

    @Test
    @DisplayName("sign up for session with existing attendance and state not PRESENT goes into update")
    void singUpWithCorrectUserAndStatePresent() {
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();
        request.speaker = false;

        doNothing().when(SESSION_SERVICE).addAttendeeToSession(session, attendance);
        doNothing().when(PERSON_SERVICE).addAttendanceToPerson(person, attendance);

        when(SERVICE.getAttendanceBySessionAndPerson(session, person)).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.findById(attendance.getId())).thenReturn(Optional.of(attendance));

        assertDoesNotThrow(
                () -> SERVICE.signUpForSession(person.getId(), session.getId(), request)
        );

        verify(ATTENDANCE_REPOSITORY, times(2))
                .findAttendanceByIdContainingAndSessionAndPerson(any(), any());
    }

    @Test
    @DisplayName("sign up for session with existing attendance and state not PRESENT goes into update")
    void singUpWithCorrectUserAndState() throws NotFoundException {
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();
        attendance.setState(PRESENT);

        when(SERVICE.getAttendanceBySessionAndPerson(session, person)).thenReturn(Optional.of(attendance));
        when(ATTENDANCE_REPOSITORY.findById(attendance.getId())).thenReturn(Optional.of(attendance));
        when(SERVICE.updateAttendance(attendance.getId(), request)).thenReturn(attendance);

        assertDoesNotThrow(
                () -> SERVICE.signUpForSession(person.getId(), session.getId(), request)
        );

        verify(ATTENDANCE_REPOSITORY, times(1)).findById(any());
    }

    private void setOneAttendanceWithIdForSession(AttendanceState state) throws NotFoundException {
        session.removeAttendee(person);
        person = mock(Person.class,
                Mockito.withSettings()
                        .useConstructor(
                                new PersonDetails(),
                                null,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                        .defaultAnswer(CALLS_REAL_METHODS)
        );
        session.addAttendee(new Attendance(state, true, person, session));

        when(SESSION_SERVICE.getSessionById(session.getId())).thenReturn(session);
        when(person.getId()).thenReturn(UUID.randomUUID());
    }

    @Test
    @DisplayName("check if attending returns true when attendance with state PRESENT exists")
    void checkIfAttendingTrue() throws NotFoundException {
        setOneAttendanceWithIdForSession(PRESENT);

        assertTrue(
                assertDoesNotThrow(
                        () -> SERVICE.checkIfAttending(session.getId(), person.getId())
                )
        );

        verify(SESSION_SERVICE, times(1)).getSessionById(any());
    }

    @Test
    @DisplayName("check if attending returns true when attendance with state CANCELED exists")
    void checkIfAttendingFalse() throws NotFoundException {
        setOneAttendanceWithIdForSession(CANCELED);

        assertFalse(
                assertDoesNotThrow(
                        () -> SERVICE.checkIfAttending(session.getId(), person.getId())
                )
        );

        verify(SESSION_SERVICE, times(1)).getSessionById(any());
    }

    @Test
    @DisplayName("check if attending returns true when attendance with state NO_SHOW exists")
    void signUpForSession() throws NotFoundException {
        setOneAttendanceWithIdForSession(NO_SHOW);

        assertFalse(
                assertDoesNotThrow(
                        () -> SERVICE.checkIfAttending(session.getId(), person.getId())
                )
        );

        verify(SESSION_SERVICE, times(1)).getSessionById(any());
    }
}
