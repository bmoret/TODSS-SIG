package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.AttendanceService;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceSpeakerRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.attendance.AttendanceStateRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.AttendanceResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

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

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendance(
            @PathVariable UUID id
    ) throws NotFoundException {
        Attendance attendance= this.SERVICE.getAttendanceById(id);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @PutMapping("/{id}/speaker")
    public ResponseEntity<AttendanceResponse> updateSpeakerAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceSpeakerRequest request
    ) throws NotFoundException {
        Attendance attendance = this.SERVICE.updateSpeakerAttendance(id, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<AttendanceResponse> updateStateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceStateRequest request
    ) throws NotFoundException {
        Attendance attendance = this.SERVICE.updateStateAttendance(id, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID id){
        this.SERVICE.deleteAttendance(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
