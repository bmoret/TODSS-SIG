package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.exceptionhandling.exception.InvalidAttendanceException;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.StateAttendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceRequest;
import javassist.NotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.snafu.todss.sig.sessies.domain.StateAttendance.PRESENT;

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
            //todo: wat hier gooien?
            throw new Exception("bestaat al");
        }
        Attendance attendance = Attendance.of(state, isSpeaker, person, session);

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
