package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.AttendanceService;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceSpeakerRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceStateRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.AttendanceResponse;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendances")
//todo  /sessions/{sessionId} Kan mogelijk beter zijn i.v.m. structuur en logica van REST pathing -jona
public class AttendanceController {
    private final AttendanceService SERVICE;

    public AttendanceController(AttendanceService attendanceService) {
        this.SERVICE = attendanceService;
    }

    private AttendanceResponse convertAttendanceToResponse(Attendance attendance) {
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getState(),
                attendance.isSpeaker(),
                attendance.getPerson(),
                attendance.getSession()
        );
    }

    private List<AttendanceResponse> convertAttendanceToListResponse(List<Attendance> attendances) {
        return attendances.stream().map(this::convertAttendanceToResponse).collect(Collectors.toList());
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendance(
            @PathVariable UUID id
    ) throws NotFoundException {
        Attendance attendance= this.SERVICE.getAttendanceById(id);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    //todo: verplaats maybe
    private List<PersonResponse> convertPersonToListResponse(List<Person> attendances) {
        return attendances.stream().map(this::convertPersonToResponse).collect(Collectors.toList());
    }

    private PersonResponse convertPersonToResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getSupervisor(),
                person.getDetails()
        );
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @GetMapping ("/{id}/speaker")
    public ResponseEntity<List<PersonResponse>> getSpeakerAttendance(
            @PathVariable UUID id
    ) throws NotFoundException {
        System.out.println(id);
        List<Person> speakers = this.SERVICE.getSpeakersFromAttendanceSession(id);
        System.out.println(speakers);

        return new ResponseEntity<>(convertPersonToListResponse(speakers), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @PutMapping("/{id}/speaker")
    public ResponseEntity<AttendanceResponse> updateSpeakerAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceSpeakerRequest request
    ) throws NotFoundException {
        Attendance attendance = this.SERVICE.updateSpeakerAttendance(id, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @PutMapping("/{id}/state")
    public ResponseEntity<AttendanceResponse> updateStateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceStateRequest request
    ) throws NotFoundException {
        Attendance attendance = this.SERVICE.updateStateAttendance(id, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID id){
        this.SERVICE.deleteAttendance(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RolesAllowed({"ROLE_MANAGER","ROLE_SECRETARY"})
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceResponse>> getAllAttendeesFromSession(@PathVariable UUID sessionId) throws NotFoundException {
        List<Attendance> attendees = this.SERVICE.getAllAttendeesFromSession(sessionId);

        return new ResponseEntity<>(convertAttendanceToListResponse(attendees), HttpStatus.OK);
    }
}
