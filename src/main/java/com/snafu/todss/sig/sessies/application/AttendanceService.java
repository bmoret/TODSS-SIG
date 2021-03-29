package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.exceptionhandling.exception.InvalidAttendanceException;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceUpdateRequest;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class AttendanceService {
    private final SpringAttendanceRepository attendanceRepository;

    public AttendanceService(SpringAttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Attendance getAttendanceById(UUID id) throws InvalidAttendanceException {
        return attendanceRepository.findById(id).orElseThrow(() -> new InvalidAttendanceException("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Attendance createAttendance(UUID personId, UUID sessionId) {
//        Person person = personRepository.;
//        Session session = sessionRepository.;

        //confirmed, absence, speaker zetten heir als ?
        return attendanceRepository.save(new Attendance(false, false, false//, person, session
        ));

    }

    public Attendance updateAttendance(UUID attendanceId, AttendanceUpdateRequest attendanceRequest) throws InvalidAttendanceException {
        Attendance attendance = getAttendanceById(attendanceId);
        attendance.setConfirmed(attendanceRequest.confirmed);
        attendance.setAbsence(attendanceRequest.absence);
        attendance.setSpeaker(attendanceRequest.speaker);

        return this.attendanceRepository.save(attendance);
    }

    public void deleteAttendance(UUID attendanceId) {
        this.attendanceRepository.deleteById(attendanceId);
    }

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

    //later extra filters zoals findAllAttendanceByPersonWhere...? & findAllAttendanceBySessionWhere...?
}
