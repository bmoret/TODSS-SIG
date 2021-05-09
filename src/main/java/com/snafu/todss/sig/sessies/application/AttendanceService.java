package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.StateAttendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceSpeakerRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceStateRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AttendanceService {
    private final SpringAttendanceRepository ATTENDANCE_REPOSITORY;
    private final PersonService PERSON_SERVICE;
    private final SessionService SESSION_SERVICE;

    public AttendanceService(SpringAttendanceRepository attendanceRepository, PersonService personService, SessionService sessionService) {
        ATTENDANCE_REPOSITORY = attendanceRepository;
        PERSON_SERVICE = personService;
        SESSION_SERVICE = sessionService;
    }

    public Attendance getAttendanceById(UUID id) throws NotFoundException {
        return ATTENDANCE_REPOSITORY.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Aanwezigheid met id '%s' bestaat niet.", id)));
    }

    public Attendance createAttendance(StateAttendance state,
                                       boolean isSpeaker,
                                       UUID personId,
                                       UUID sessionId) throws Exception {
        Person person = this.PERSON_SERVICE.getPerson(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);
        if( ATTENDANCE_REPOSITORY.findAttendanceByIdContainingAndPersonAndSession(person, session).isPresent() ) {
            throw new Exception("bestaat al");
        }
        Attendance attendance = Attendance.of(state, isSpeaker, person, session);

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    public Attendance updateSpeakerAttendance(UUID id, AttendanceSpeakerRequest attendanceSpeakerRequest) throws NotFoundException {
        Attendance attendance = getAttendanceById(id);
        attendance.setSpeaker(attendanceSpeakerRequest.speaker);

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    public Attendance updateStateAttendance(UUID id, AttendanceStateRequest attendanceStateRequest) throws NotFoundException {
        Attendance attendance = getAttendanceById(id);
        attendance.setState(attendanceStateRequest.state);

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    public void deleteAttendance(UUID id) {
        this.ATTENDANCE_REPOSITORY.deleteById(id);
    }

    public List<Person> getSpeakersFromAttendanceSession(UUID id) throws NotFoundException {
        Session session = this.SESSION_SERVICE.getSessionById(id);
        List<Attendance> attendances = this.ATTENDANCE_REPOSITORY.findAttendancesBySession(session);
        List<Person> speakers = new ArrayList<>();
        attendances.forEach(
                attendance -> {
                    if (attendance.isSpeaker()) {
                        speakers.add(attendance.getPerson());
                    }
                }
        );
        return speakers;
    }
}
