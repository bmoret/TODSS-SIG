package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.exceptionhandling.exception.InvalidAttendanceException;
import com.snafu.todss.sig.sessies.application.AttendanceService;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.AttendanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                attendance.getAttendanceId(),
                attendance.isConfirmed(),
                attendance.isAbsent(),
                attendance.isSpeaker(),
                attendance.getPerson(),
                attendance.getSession()
        );
    }

    private List<AttendanceResponse> convertAttendanceListToResponse(List<Attendance> attendances) {
        return attendances.stream()
                .map(this::convertAttendanceToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendances() {
        List<Attendance> allAttendance = this.SERVICE.getAllAttendance();

        return new ResponseEntity<>(convertAttendanceListToResponse(allAttendance), HttpStatus.OK);
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> getAttendance(
            @PathVariable UUID attendanceId
    ) throws InvalidAttendanceException {
        Attendance attendance= this.SERVICE.getAttendanceById(attendanceId);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable UUID attendanceId,
            @Valid @RequestBody AttendanceRequest request
    ) throws InvalidAttendanceException {
        Attendance attendance = this.SERVICE.updateAttendance(attendanceId, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID attendanceId){
        this.SERVICE.deleteAttendance(attendanceId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    //Stashed for later implementations - Jona

//    @PostMapping()
//    public ResponseEntity<AttendanceResponse> createAttendance(
//            @RequestBody AttendanceRequest request
//     ) {
//        Attendance attendance = this.SERVICE.SignUpForSession(request.getPerson(), request.getSession());
//
//        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.CREATED);
//    }


//
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



    //todo: Patch functies voor absent aanwezig etc of puur put? -Jona

}
