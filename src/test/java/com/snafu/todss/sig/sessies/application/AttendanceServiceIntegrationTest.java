package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.AttendanceState;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceRequest;
import com.sun.jdi.request.DuplicateRequestException;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.snafu.todss.sig.sessies.domain.AttendanceState.*;
import static com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN;
import static com.snafu.todss.sig.sessies.domain.person.enums.Role.MANAGER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AttendanceServiceIntegrationTest {
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
    private Person person;
    private Session session;

    @BeforeAll
    static void init() {
    }

    @BeforeEach
    void setup() {

        person = PERSON_REPOSITORY.save(new PersonBuilder()
                .setEmail("t_a")
                .setFirstname("ttt")
                .setLastname("a")
                .setExpertise("none")
                .setEmployedSince(LocalDate.of(2021, 1, 1))
                .setBranch(VIANEN)
                .setRole(MANAGER)
                .build());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subjectttt";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = SIG_REPOSITORY.save(new SpecialInterestGroup("name", null, new ArrayList<>(), new ArrayList<>()));
        session = SESSION_REPOSITORY.save(new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        ));

        attendance = ATTENDANCE_REPOSITORY.save(new Attendance(PRESENT, false, person, session));
        session.addAttendee(attendance);
        session = this.SESSION_REPOSITORY.save(session);
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
        assertEquals("ttt", testAttendance.getPerson().getDetails().getFirstname());
        assertEquals("Subjectttt", testAttendance.getSession().getDetails().getSubject());
    }

    @Test
    @DisplayName("get attendance by id throws when not found")
    void getAttendanceByIdThrows() {
        assertThrows(
                NotFoundException.class,
                () -> ATTENDANCE_SERVICE.getAttendanceById(UUID.randomUUID())
        );
    }

    private void clearAttendancesFromRepository() {
        session.removeAttendee(person);
        person.removeAttendance(attendance);
        ATTENDANCE_REPOSITORY.save(attendance);
        ATTENDANCE_REPOSITORY.deleteAll();
    }

    @Test
    @DisplayName("create attendance")
    void createAttendance() {
        clearAttendancesFromRepository();

        assertDoesNotThrow(
                () -> ATTENDANCE_SERVICE.createAttendance(PRESENT, false, attendance.getSession().getId(), attendance.getPerson().getId())
        );
    }

    @Test
    @DisplayName("create attendance with info of already existing attendance")
    void createAttendanceThrowsWhenAlreadyExists() {
        assertThrows(
                DuplicateRequestException.class,
                () -> ATTENDANCE_SERVICE.createAttendance(PRESENT, false, attendance.getSession().getId(), attendance.getPerson().getId())
        );
    }

    private static Stream<Arguments> createIncorrectAttendanceExamples() {
        Person person = new PersonBuilder()
                .setEmail("t_a")
                .setFirstname("t")
                .setLastname("a")
                .setExpertise("none")
                .setEmployedSince(LocalDate.of(2021, 1, 1))
                .setBranch(VIANEN)
                .setRole(MANAGER)
                .build();

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
                address,
                null
        );
        SpecialInterestGroup sig = new SpecialInterestGroup("name", null, new ArrayList<>(), new ArrayList<>());


        return Stream.of(
                Arguments.of(
                        PRESENT,
                        null,
                        session,
                        sig
                ),
                Arguments.of(
                        PRESENT,
                        person,
                        null,
                        sig
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createIncorrectAttendanceExamples")
    @DisplayName("create attendance throws when info is incorrect")
    void createAttendanceThrowsWhenIncorrectInfo(AttendanceState attendanceState,
                                                 Person person,
                                                 Session session,
                                                 SpecialInterestGroup sig) {
        UUID personId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        sig = SIG_REPOSITORY.save(sig);

        if (person != null) {
            personId = PERSON_REPOSITORY.save(person).getId();
        }
        if (session != null) {
            session.setSig(sig);
            sessionId = SESSION_REPOSITORY.save(session).getId();
        }
        UUID finalPersonId = personId;
        UUID finalSessionId = sessionId;
        assertThrows(
                NotFoundException.class,
                () -> ATTENDANCE_SERVICE.createAttendance(attendanceState, false, finalSessionId, finalPersonId)
        );
    }

    @Test
    @DisplayName("Create existing attendance")
    void createExistingAttendance_ThrowsDuplicateRequestException() {
        assertThrows(
                DuplicateRequestException.class,
                () -> ATTENDANCE_SERVICE.createAttendance(NO_SHOW, false, session.getId(), person.getId())
        );
    }

    @Test
    @DisplayName("update speaker of attendance")
    void updateSpeakerAttendance() {
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();
        request.speaker = true;

        Attendance attendance = assertDoesNotThrow(() -> ATTENDANCE_SERVICE.updateAttendance(this.attendance.getId(), request));

        assertTrue(attendance.isSpeaker());
    }

    @Test
    @DisplayName("update attendance throws when attendance not found")
    void updateSpeakerAttendanceThrows() {
        AttendanceRequest request = new AttendanceRequest();
        request.speaker = true;
        assertThrows(
                NotFoundException.class,
                () -> ATTENDANCE_SERVICE.updateAttendance(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("update state of attendance")
    void updateAttendance() {
        AttendanceRequest request = new AttendanceRequest();
        request.state = NO_SHOW.toString();

        Attendance attendance = assertDoesNotThrow(() -> ATTENDANCE_SERVICE.updateAttendance(this.attendance.getId(), request));

        assertEquals(NO_SHOW, attendance.getState());
    }

    @Test
    @DisplayName("update attendance throws when attendance not found")
    void updateAttendanceThrows() {
        AttendanceRequest request = new AttendanceRequest();
        request.state = NO_SHOW.toString();
        assertThrows(
                NotFoundException.class,
                () -> ATTENDANCE_SERVICE.updateAttendance(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("delete attendance does not throw and ")
    void deleteAttendance() {
        assertDoesNotThrow(() -> ATTENDANCE_SERVICE.deleteAttendance(attendance.getId()));
        assertEquals(Collections.emptyList(), ATTENDANCE_REPOSITORY.findAll());
    }

    @Test
    @DisplayName("get speakers from a session")
    void getSpeakers() {
        PersonBuilder pb = new PersonBuilder();
        pb.setEmail("t_a");
        pb.setFirstname("a");
        pb.setLastname("t");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021, 1, 1));
        pb.setBranch(VIANEN);
        pb.setRole(MANAGER);
        Person person = PERSON_REPOSITORY.save(pb.build());
        Person person1 = PERSON_REPOSITORY.save(pb.build());

        ATTENDANCE_REPOSITORY.save(
                new Attendance(PRESENT, true, person, attendance.getSession()
                ));
        ATTENDANCE_REPOSITORY.save(
                new Attendance(PRESENT, true, person1, attendance.getSession()
                ));

        List<Person> speakers = assertDoesNotThrow(
                () -> ATTENDANCE_SERVICE.getSpeakersFromAttendanceSession(attendance.getSession().getId())
        );

        assertEquals(2, speakers.size());
    }

    @Test
    @DisplayName("check if attendance exists for person / session combination and returns true when it does")
    void checkAttendanceBySessionPersonTrue() {
        assertTrue(
                assertDoesNotThrow(
                        () -> ATTENDANCE_SERVICE.checkIfAttending(
                                attendance.getSession().getId(), person.getId())
                )
        );
    }

    @Test
    @DisplayName("check if attendance exists for person / session combination and returns false when it does not")
    void checkIfAttendanceExistsReturnsFalse() {
        PersonBuilder pb = new PersonBuilder();
        pb.setEmail("t_a");
        pb.setFirstname("a");
        pb.setLastname("t");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021, 1, 1));
        pb.setBranch(VIANEN);
        pb.setRole(MANAGER);
        Person person = PERSON_REPOSITORY.save(pb.build());

        assertFalse(
                assertDoesNotThrow(
                        () -> ATTENDANCE_SERVICE.checkIfAttending(
                                attendance.getSession().getId(), person.getId())
                )
        );
    }

    @Test
    @DisplayName("check if attendance exists for person / session combination")
    void checkIfAttendanceExists() {
        assertTrue(
                assertDoesNotThrow(
                        () -> ATTENDANCE_SERVICE.getAttendanceBySessionAndPerson(
                                attendance.getSession(), attendance.getPerson()).isPresent()
                )
        );
    }

    @Test
    @DisplayName("check if attendance exists for person / session combination")
    void checkAttendanceBySessionPersonFalse() {
        PersonBuilder pb = new PersonBuilder();
        pb.setEmail("t_a");
        pb.setFirstname("a");
        pb.setLastname("t");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021, 1, 1));
        pb.setBranch(VIANEN);
        pb.setRole(MANAGER);
        Person person1 = PERSON_REPOSITORY.save(pb.build());

        assertFalse(
                assertDoesNotThrow(
                        () -> ATTENDANCE_SERVICE.getAttendanceBySessionAndPerson(
                                attendance.getSession(), person1).isPresent()
                )
        );
    }

    @Test
    @DisplayName("create attendance by useing signUpForSession")
    void singUpForSessionsWithNoAttendance() {
        ATTENDANCE_REPOSITORY.deleteAll();
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();
        request.speaker = false;

        assertDoesNotThrow(
                () -> ATTENDANCE_SERVICE.signUpForSession(attendance.getSession().getId(), attendance.getPerson().getId(), request)
        );
    }

    @Test
    @DisplayName("update attendance by useing signUpForSession")
    void singUpForSessionWithAttendanceAndStateNotPresent() {
        AttendanceRequest request = new AttendanceRequest();
        request.state = PRESENT.toString();
        request.speaker = false;
        attendance.setState(CANCELED);
        ATTENDANCE_REPOSITORY.save(attendance);

        assertDoesNotThrow(
                () -> ATTENDANCE_SERVICE.signUpForSession(attendance.getSession().getId(), attendance.getPerson().getId(), request)
        );
        assertEquals(PRESENT,
                assertDoesNotThrow(
                        () -> ATTENDANCE_SERVICE.getAttendanceById(attendance.getId()).getState()
                )
        );
    }
}


