package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.AttendanceState;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.PresenceRequest;
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

    public AttendanceService(
            SpringAttendanceRepository attendanceRepository,
            PersonService personService,
            SessionService sessionService
    ) {
        ATTENDANCE_REPOSITORY = attendanceRepository;
        PERSON_SERVICE = personService;
        SESSION_SERVICE = sessionService;
    }

    public List<Attendance> getAllAttendeesFromSession(UUID id) throws NotFoundException {
        Session session = SESSION_SERVICE.getSessionById(id);

        return this.ATTENDANCE_REPOSITORY.findAttendancesBySession(session);
    }

    public Attendance getAttendanceById(UUID id) throws NotFoundException {
        return ATTENDANCE_REPOSITORY.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Aanwezigheid met id '%s' bestaat niet.", id)));
    }
    public Optional<Attendance> getAttendanceBySessionAndPerson(Session session, Person person) {
        return ATTENDANCE_REPOSITORY.findAttendanceByIdContainingAndSessionAndPerson(session, person);
    }

    public Attendance signUpForSession(
            UUID sessionId,
            UUID personId,
            AttendanceRequest request
    ) throws DuplicateRequestException, NotFoundException {
        Person person = this.PERSON_SERVICE.getPersonById(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);
        Optional<Attendance> attendance = getAttendanceBySessionAndPerson(session, person);
        if(attendance.isPresent()) {
            return updateAttendance(attendance.get().getId(),request);
        }
        return createAttendance(getAttendanceStateOfString(request.state), request.speaker,sessionId, personId);
    }

    public Attendance createAttendance(AttendanceState state,
                                       boolean isSpeaker,
                                       UUID sessionId,
                                       UUID personId
    ) throws DuplicateRequestException, NotFoundException {
        Person person = this.PERSON_SERVICE.getPersonById(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);
        if(getAttendanceBySessionAndPerson(session, person).isPresent()) {
            throw new DuplicateRequestException("Je bent al aangemeld voor deze sessie.");
        }
        Attendance attendance = this.ATTENDANCE_REPOSITORY.save(new Attendance(state, isSpeaker, person, session));
        this.SESSION_SERVICE.addAttendeeToSession(session, attendance);
        this.PERSON_SERVICE.addAttendanceToPerson(person, attendance);

        return attendance;
    }

    public Attendance updateAttendance(UUID id, AttendanceRequest attendanceRequest) throws NotFoundException {
        Attendance attendance = this.getAttendanceById(id);
        attendance.setState(getAttendanceStateOfString(attendanceRequest.state));
        attendance.setSpeaker(attendanceRequest.speaker);

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    public Attendance updatePresence(UUID id, PresenceRequest presenceRequest) throws NotFoundException {
        Attendance attendance = this.getAttendanceById(id);
        Session session = attendance.getSession();
        if (session.getState() != SessionState.ONGOING && session.getState() != SessionState.ENDED) {
            throw new IllegalArgumentException(
                    "Cannot change the state of attendance when the session has not begun yet.");
        }
        if (presenceRequest.isPresent) {
            attendance.setState(AttendanceState.PRESENT);
        } else {
            attendance.setState(AttendanceState.NO_SHOW);
        }

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    private AttendanceState getAttendanceStateOfString(String state) {
        try {
            return AttendanceState.valueOf(state);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("No state with name '%s' exists", state));
        }
    }

    public void deleteAttendance(UUID id) throws NotFoundException {
        Attendance attendance = getAttendanceById(id);
        SESSION_SERVICE.removeAttendeeFromSession(attendance.getSession(), attendance.getPerson());
        PERSON_SERVICE.removeAttendanceFromPerson(attendance.getPerson(), attendance);

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

    public boolean checkIfAttending(UUID sessionId, UUID personId) throws NotFoundException {
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);
        Optional<Attendance> attendance = session.getAttendances().stream()
                .filter(a -> a.getPerson().getId().equals(personId))
                .findAny();
        return attendance.isPresent() && attendance.get().getState() == AttendanceState.PRESENT;
    }
}
