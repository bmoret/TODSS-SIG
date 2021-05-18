package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.AttendanceState;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceRequest;
import com.sun.jdi.request.DuplicateRequestException;
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

    public boolean checkAttendanceBySessionAndPerson(Person person, Session session) {
        return ATTENDANCE_REPOSITORY.findAttendanceByIdContainingAndPersonAndSession(person, session).isPresent();
    }

    public Attendance createAttendance(AttendanceState state,
                                       boolean isSpeaker,
                                       UUID personId,
                                       UUID sessionId) throws DuplicateRequestException, NotFoundException {
        Person person = this.PERSON_SERVICE.getPerson(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);
        if( checkAttendanceBySessionAndPerson(person, session) ) {
            throw new DuplicateRequestException("Je bent al aangemeld voor deze sessie.");
        }
        Attendance attendance = new Attendance(state, isSpeaker, person, session);

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    public Attendance updateAttendance(UUID id, AttendanceRequest attendanceRequest) throws NotFoundException {
        Attendance attendance = getAttendanceById(id);
        attendance.setState(attendanceRequest.state);
        attendance.setSpeaker(attendanceRequest.speaker);

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

    public Boolean checkIfAttendanceExists(UUID sessionId, UUID personId) throws NotFoundException {
        Session session = SESSION_SERVICE.getSessionById(sessionId);
        Person person = PERSON_SERVICE.getPerson(personId);

        return checkAttendanceBySessionAndPerson(person, session);
    }
}
