package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.exceptionhandling.exception.InvalidAttendanceException;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AttendanceService {
    private final SpringAttendanceRepository ATTENDANCE_REPOSITORY;
    private final PersonService PERSON_SERVICE;
    private final SessionService SESSION_SERVICE;

    public AttendanceService(SpringAttendanceRepository attendanceRepository, PersonService personService, SessionService sessionService) {
        this.ATTENDANCE_REPOSITORY = attendanceRepository;
        PERSON_SERVICE = personService;
        SESSION_SERVICE = sessionService;
    }

    public Attendance getAttendanceById(UUID id) throws InvalidAttendanceException {
        return ATTENDANCE_REPOSITORY.findById(id)
                .orElseThrow(() -> new InvalidAttendanceException(String.format("Aanwezigheid met id '%s' bestaat niet.", id)));
    }

    public List<Attendance> getAllAttendance() {
        return ATTENDANCE_REPOSITORY.findAll();
    }

    public Attendance createAttendance(UUID personId, UUID sessionId) throws NotFoundException {
        Person person = this.PERSON_SERVICE.getPerson(personId);
        Session session = this.SESSION_SERVICE.getSessionById(sessionId);
        Attendance attendance = Attendance.of(person, session);

        return ATTENDANCE_REPOSITORY.save(attendance);
    }

    public Attendance updateAttendance(UUID attendanceId, AttendanceRequest attendanceRequest) throws InvalidAttendanceException {
        Attendance attendance = getAttendanceById(attendanceId);
        attendance.setConfirmed(attendanceRequest.isConfirmed);
        attendance.setAbsent(attendanceRequest.isAbsence);
        attendance.setSpeaker(attendanceRequest.isSpeaker);

        return this.ATTENDANCE_REPOSITORY.save(attendance);
    }

    public void deleteAttendance(UUID attendanceId) {
        this.ATTENDANCE_REPOSITORY.deleteById(attendanceId);
    }



    //Stashed for later implementations (niet nodig geweest om nu al te maken hoor thomas :)) - Jona

//    public Attendance findAllAttendanceByPerson(Person person) {
//        //zoek of persoon bestaat in service
//        return attendanceRepository.findAllByPerson(person).orElseThrow(() -> new InvalidAttendanceException(person.getName()+" heeft geen kennissessies gevolgd."));
//    }
//
//    public Attendance findAllAttendanceBySession(Session session) {
//        //zoek of sessie bestaat in service
//        return attendanceRepository.findAllBySession(session).orElseThrow(() -> new InvalidAttendanceException(session.getIETS()+" heeft geen opgeslagen aanwezigheid."));
//    }

//    public Attendance SignUpForSession(Person person, Session session) {
//        //lezerstatus regelen waar?
//        Person personToSignUp = personRepository.findPersonById(person.getId());
//        Session sessionToSignUpTo = personRepository.findPersonById(person.getId());
//        Attendance attendance = new Attendance(false, false,false, personToSignUp, sessionToSignUpTo);
//
//        return attendance;
//    }

    //later extra filters zoals findAllAttendanceByPersonWhere...? & findAllAttendanceBySessionWhere...? -thomas
    //kan, met queryparams en results daarop filteren, params op query meegeven -jona
}
