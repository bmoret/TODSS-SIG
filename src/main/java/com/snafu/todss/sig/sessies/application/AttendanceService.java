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
import java.util.Optional;
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

    public boolean checkIfAttending(UUID sessionId, UUID personId) throws NotFoundException {
        Person person = this.PERSON_SERVICE.getPerson(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);

        Optional<Attendance> attendance = getAttendanceBySessionAndPerson(session, person);

        return attendance.isPresent() && attendance.get().getState() == AttendanceState.PRESENT;
    }

    public Optional<Attendance> getAttendanceBySessionAndPerson(Session session, Person person) {
        return ATTENDANCE_REPOSITORY.findAttendanceByIdContainingAndSessionAndPerson(session, person);
    }

    public Attendance signUpForSession(
            UUID sessionId,
            UUID personId,
            AttendanceRequest request
    ) throws DuplicateRequestException, NotFoundException {
        Person person = this.PERSON_SERVICE.getPerson(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);
        Optional<Attendance> attendance = getAttendanceBySessionAndPerson(session, person);
        if(attendance.isPresent()) {
            return updateAttendance(attendance.get().getId(),request);
        }
        return createAttendance(request.state, request.speaker,sessionId, personId);
    }

    public Attendance createAttendance(AttendanceState state,
                                       boolean isSpeaker,
                                       UUID sessionId,
                                       UUID personId
    ) throws DuplicateRequestException, NotFoundException {
        Person person = this.PERSON_SERVICE.getPerson(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);

        if(getAttendanceBySessionAndPerson(session, person).isPresent()) {
            throw new DuplicateRequestException("Je bent al aangemeld voor deze sessie.");
        }
        Attendance attendance = new Attendance(state, isSpeaker, person, session);

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    public Attendance updateAttendance(UUID id, AttendanceRequest attendanceRequest) throws NotFoundException {
        Attendance attendance = this.getAttendanceById(id);
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
}
