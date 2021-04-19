package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.StateAttendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceSpeakerRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceStateRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

import static com.snafu.todss.sig.sessies.domain.StateAttendance.*;
import static com.snafu.todss.sig.sessies.domain.person.enums.Branch.*;
import static com.snafu.todss.sig.sessies.domain.person.enums.Role.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AttendanceIntergrationServiceTest {
    @Autowired
    private AttendanceService ATTENDANCE_SERVICE;
    @Autowired
    private SpringPersonRepository PERSON_REPOSITORY;
    @Autowired
    private SessionRepository SESSION_REPOSITORY;
    @Autowired
    private SpecialInterestGroupRepository SIG_REPOSITORY;
    @Autowired
    private SpringAttendanceRepository ATTENDANCE_REPOSITORY;

    private Attendance attendance;

    @BeforeAll
    static void init() {
    }

    @BeforeEach
    void setup() {
        ATTENDANCE_REPOSITORY.deleteAll();
        SESSION_REPOSITORY.deleteAll();
        SIG_REPOSITORY.deleteAll();
        PERSON_REPOSITORY.deleteAll();

        PersonBuilder pb = new PersonBuilder();
        pb.setEmail("t_a");
        pb.setFirstname("t");
        pb.setLastname("a");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021,1,1));
        pb.setBranch(VIANEN);
        pb.setRole(MANAGER);
        Person person = PERSON_REPOSITORY.save(pb.build());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = SIG_REPOSITORY.save(new SpecialInterestGroup());

        Session session = this.SESSION_REPOSITORY.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address
                )
        );
        attendance = ATTENDANCE_REPOSITORY.save(new Attendance(PRESENT, false, person, session));
    }

    @AfterEach
    void tearDown() {
        ATTENDANCE_REPOSITORY.deleteAll();
        SESSION_REPOSITORY.deleteAll();
        SIG_REPOSITORY.deleteAll();
        PERSON_REPOSITORY.deleteAll();
    }

    @Test
    @DisplayName("get attendance by id")
    void getAttendanceById() {
        Attendance testAttendance = assertDoesNotThrow(() -> ATTENDANCE_SERVICE.getAttendanceById(attendance.getId()));

        assertEquals(PRESENT, testAttendance.getState());
        assertFalse(testAttendance.isSpeaker());
        assertEquals("t", testAttendance.getPerson().getDetails().getFirstname());
        assertEquals("Subject", testAttendance.getSession().getDetails().getSubject());
    }

    @Test
    @DisplayName("get attendance by id throws when not found")
    void getAttendanceByIdThrows() {
        assertThrows(
                NotFoundException.class,
                () -> ATTENDANCE_SERVICE.getAttendanceById(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("create attendance")
    void createAttendance() {
        ATTENDANCE_REPOSITORY.deleteAll();

        assertDoesNotThrow(
                () -> ATTENDANCE_SERVICE.createAttendance(PRESENT, false, attendance.getPerson().getId(), attendance.getSession().getId())
        );
    }

    @Test
    @DisplayName("create attendance with info of already existing attendance")
    void createAttendanceThrowsWhenAlreadyExists() {
        assertThrows(
                Exception.class,
                () -> ATTENDANCE_SERVICE.createAttendance(PRESENT, false, attendance.getPerson().getId(), attendance.getSession().getId())
        );
    }

    private static Stream<Arguments> createIncorrectAttendanceExamples() {
        PersonBuilder pb = new PersonBuilder();
        pb.setEmail("t_a");
        pb.setFirstname("t");
        pb.setLastname("a");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021,1,1));
        pb.setBranch(VIANEN);
        pb.setRole(MANAGER);
        Person person = pb.build();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";

        Session session = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address
        );

        return Stream.of(
                Arguments.of(
                        null,
                        false,
                        person.getId(),
                        session.getId()
                ),
                Arguments.of(
                        PRESENT,
                        false,
                        null,
                        session.getId()
                ),
                Arguments.of(
                        PRESENT,
                        false,
                        person.getId(),
                        null
                ),
                Arguments.of(
                        PRESENT,
                        false,
                        person.getId(),
                        UUID.randomUUID()
                ),
                Arguments.of(
                        PRESENT,
                        false,
                        UUID.randomUUID(),
                        session.getId()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createIncorrectAttendanceExamples")
    @DisplayName("create attendance throws when info is incorrect")
    void createAttendanceThrowsWhenIncorrectInfo(StateAttendance stateAttendance,
                                                 boolean speaker,
                                                 UUID personId,
                                                 UUID sessionId) {
        assertThrows(
                Exception.class,
                () -> ATTENDANCE_SERVICE.createAttendance(stateAttendance, speaker, personId, sessionId)
        );
    }

    @Test
    @DisplayName("update speaker of attendance")
    void updateSpeakerAttendance() {
        AttendanceSpeakerRequest request = new AttendanceSpeakerRequest();
        request.speaker = true;

        Attendance attendance = assertDoesNotThrow(() -> ATTENDANCE_SERVICE.updateSpeakerAttendance(this.attendance.getId(), request));

        assertTrue(attendance.isSpeaker());
    }

    @Test
    @DisplayName("update attendance throws when attendance not found")
    void updateSpeakerAttendanceThrows() {
        AttendanceSpeakerRequest request = new AttendanceSpeakerRequest();
        request.speaker = true;
        assertThrows(NotFoundException.class,
                () -> ATTENDANCE_SERVICE.updateSpeakerAttendance(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("update state of attendance")
    void updateAttendance() {
        AttendanceStateRequest request = new AttendanceStateRequest();
        request.state = NO_SHOW;

        Attendance attendance = assertDoesNotThrow(() -> ATTENDANCE_SERVICE.updateStateAttendance(this.attendance.getId(), request));

        assertEquals(NO_SHOW, attendance.getState());
    }

    @Test
    @DisplayName("update attendance throws when attendance not found")
    void updateAttendanceThrows() {
        AttendanceStateRequest request = new AttendanceStateRequest();
        request.state = NO_SHOW;
        assertThrows(NotFoundException.class,
                () -> ATTENDANCE_SERVICE.updateStateAttendance(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("delete attendance does not throw and ")
    void deleteAttendance() {
        assertDoesNotThrow(() -> ATTENDANCE_SERVICE.deleteAttendance(attendance.getId()));
        assertEquals(Collections.emptyList(), ATTENDANCE_REPOSITORY.findAll());
    }
}