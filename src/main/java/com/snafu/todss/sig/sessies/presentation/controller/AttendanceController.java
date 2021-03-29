package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.exceptionhandling.exception.InvalidAttendanceException;
import com.snafu.todss.sig.sessies.application.AttendanceService;
import com.snafu.todss.sig.sessies.domain.Attendance;
//import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceCreateRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceUpdateRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.AttendanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttendanceController {
    private final AttendanceService SERVICE;

    public AttendanceController(AttendanceService attendanceService) {
        this.SERVICE = attendanceService;
    }

    private AttendanceResponse convertAttendanceToResponse(Attendance attendance) {
        return new AttendanceResponse(
                attendance.getAttendanceId(),
                attendance.isConfirmed(),
                attendance.isAbsence(),
                attendance.isSpeaker()//, attendance.getPerson(), attendance.getSession()
        );
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> getAttendance(
            @PathVariable UUID attendanceId
    ) throws InvalidAttendanceException {
        Attendance attendance= this.SERVICE.getAttendanceById(attendanceId);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendances() {
        List<Attendance> allAttendance = this.SERVICE.getAllAttendance();
        List<AttendanceResponse> responses = new ArrayList<>();
        for (Attendance attendance : allAttendance) {
            responses.add(convertAttendanceToResponse(attendance));
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


//    @GetMapping("/{personId}")
//    public ResponseEntity<List<AttendanceResponse>> getAttendanceByPerson(
//      @PathVariable UUID personId
//    ) throws InvalidAttendanceException {
//        List<Attendance> attendanceByPerson = this.SERVICE.findAllAttendanceByPerson(perosnId);
//        List<AttendanceResponse> responses = new ArrayList<>();
//        for (Attendance attendance : attendanceByPerson) {
//            responses.add(convertAttendanceToResponse(attendance));
//        }
//
//        return new ResponseEntity<>(responses, HttpStatus.OK);
//    }
//
//    @GetMapping("/{sessionId}")
//    public ResponseEntity<List<AttendanceResponse>> getAttendanceBySession(
//            @PathVariable UUID sessionId
//    ) throws InvalidAttendanceException {
//        List<Attendance> attendanceBySession = this.SERVICE.findAllAttendanceBySession(sessionId);
//        List<AttendanceResponse> responses = new ArrayList<>();
//        for (Attendance attendance : attendanceBySession) {
//            responses.add(convertAttendanceToResponse(attendance));
//        }
//
//        return new ResponseEntity<>(responses, HttpStatus.OK);
//    }
//
//    @PostMapping("verzin iets")
//    public ResponseEntity<AttendanceResponse> createAttendance(
//            @RequestBody AttendanceCreateRequest request
//            ) {
//        Attendance attendance = this.SERVICE.SignUpForSession(request.getPerson(), request.getSession());
//
//        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.CREATED);
//    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable UUID attendanceId,
            @Valid @RequestBody AttendanceUpdateRequest request
            ) throws InvalidAttendanceException {
        Attendance attendance = this.SERVICE.updateAttendance(attendanceId, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID attendanceId){
        this.SERVICE.deleteAttendance(attendanceId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
