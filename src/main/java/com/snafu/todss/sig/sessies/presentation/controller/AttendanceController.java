package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.exceptionhandling.exception.InvalidAttendanceException;
import com.snafu.todss.sig.sessies.application.AttendanceService;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.AttendanceResponse;
import javassist.NotFoundException;
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

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceRequest request
    ) throws NotFoundException {
        Attendance attendance = this.SERVICE.updateAttendance(id, request);

        return new ResponseEntity<>(convertAttendanceToResponse(attendance), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable UUID id){
        this.SERVICE.deleteAttendance(id);

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
