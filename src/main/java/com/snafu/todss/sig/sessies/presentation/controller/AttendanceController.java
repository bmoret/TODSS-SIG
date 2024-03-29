package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.AttendanceService;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.PresenceRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.AttendanceResponse;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendances")
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
        List<Person> speakers = this.SERVICE.getSpeakersFromAttendanceSession(id);

        return new ResponseEntity<>(convertPersonToListResponse(speakers), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER","ROLE_SECRETARY", "ROLE_ORGANIZER","ROLE_ADMINISTRATOR"})
    @PatchMapping("/{id}/presence")
    public ResponseEntity<AttendanceResponse> updatePresence(
            @PathVariable UUID id,
            @Valid @RequestBody PresenceRequest request
    ) throws NotFoundException {
        Attendance attendance = this.SERVICE.updatePresence(id, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @PutMapping("/{id}/update")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceRequest request
    ) throws NotFoundException {
        Attendance attendance = this.SERVICE.updateAttendance(id, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID id) throws NotFoundException {
        this.SERVICE.deleteAttendance(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RolesAllowed({"ROLE_MANAGER","ROLE_SECRETARY", "ROLE_ADMINISTRATOR"})
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceResponse>> getAllAttendeesFromSession(
            @PathVariable UUID sessionId
    ) throws NotFoundException {
        List<Attendance> attendees = this.SERVICE.getAllAttendeesFromSession(sessionId);

        return new ResponseEntity<>(convertAttendanceToListResponse(attendees), HttpStatus.OK);
    }


    @PermitAll
    @GetMapping("/{sessionId}/{personId}")
    public ResponseEntity<Boolean> checkIfAttending(
            @PathVariable UUID sessionId, @PathVariable UUID personId
    ) throws NotFoundException {
        Boolean present = this.SERVICE.checkIfAttending(sessionId, personId);

        return new ResponseEntity<>(present, HttpStatus.OK);
    }

    @PermitAll
    @PatchMapping("/{sessionId}/{personId}")
    public ResponseEntity<AttendanceResponse> signUpForAttendance(
            @PathVariable UUID sessionId, @PathVariable UUID personId, @Valid @RequestBody AttendanceRequest request
    ) throws NotFoundException {
        Attendance attendance = SERVICE.signUpForSession(sessionId, personId, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }
}
