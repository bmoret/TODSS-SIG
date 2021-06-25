package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.SessionService;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.SessionResponse;
import com.snafu.todss.sig.sessies.presentation.dto.response.SpecialInterestGroupResponse;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.snafu.todss.sig.sessies.presentation.dto.converter.SessionConverter.convertSessionListToResponse;
import static com.snafu.todss.sig.sessies.presentation.dto.converter.SessionConverter.convertSessionToResponse;

@RestController
@RequestMapping("/sessions")
public class
SessionController {
    private final SessionService SERVICE;

    public SessionController(SessionService sessionService) {
        this.SERVICE = sessionService;
    }

    private SessionResponse convertToSessionResponse(Session session) {
        SessionResponse response = convertSessionToResponse(session);
        SpecialInterestGroupResponse sigResponse = new ModelMapper().map(session.getSig(), SpecialInterestGroupResponse.class);
        response.setSpecialInterestGroup(sigResponse);
        return response;
    }

    @PermitAll
    @GetMapping("/future")
    public ResponseEntity<List<SessionResponse>> getAllFutureSessions(Authentication authentication) {
        UserDetails profile = (UserDetails) authentication.getPrincipal();
        List<Session> sessions = this.SERVICE.getAllFutureSessions(profile.getUsername());

        return new ResponseEntity<>(convertSessionListToResponse(sessions), HttpStatus.OK);
    }

    @PermitAll
    @GetMapping("/history")
    public ResponseEntity<List<SessionResponse>> getAllHistoricalSessions(Authentication authentication) {
        UserDetails profile = (UserDetails) authentication.getPrincipal();
        List<Session> sessions = this.SERVICE.getAllHistoricalSessions(profile.getUsername());

        return new ResponseEntity<>(convertSessionListToResponse(sessions), HttpStatus.OK);
    }

    @PermitAll
    @GetMapping("/future/{personId}")
    public ResponseEntity<List<SessionResponse>> getFutureSessionsOfPerson(@PathVariable UUID personId, Authentication authentication) throws NotFoundException, IllegalAccessException {
        UserDetails profile = (UserDetails) authentication.getPrincipal();
        List<Session> sessions = this.SERVICE.getFutureSessionsOfPerson(profile.getUsername(), personId);

        return new ResponseEntity<>(convertSessionListToResponse(sessions), HttpStatus.OK);
    }

    @PermitAll
    @GetMapping("/history/{personId}")
    public ResponseEntity<List<SessionResponse>> getHistorySessionsOfPerson(@PathVariable UUID personId, Authentication authentication) throws NotFoundException, IllegalAccessException {
        UserDetails profile = (UserDetails) authentication.getPrincipal();
        List<Session> sessions = this.SERVICE.getHistorySessionsOfPerson(profile.getUsername(), personId);

        return new ResponseEntity<>(convertSessionListToResponse(sessions), HttpStatus.OK);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        List<Session> sessions = this.SERVICE.getAllSessions();

        return new ResponseEntity<>(convertSessionListToResponse(sessions), HttpStatus.OK);
    }

    @PermitAll
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable UUID sessionId) throws NotFoundException {
        Session session = this.SERVICE.getSessionById(sessionId);

        return new ResponseEntity<>(convertToSessionResponse(session), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER","ROLE_SECRETARY", "ROLE_ORGANIZER", "ROLE_ADMINISTRATOR"})
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest sessionRequest) throws NotFoundException {
        Session session = this.SERVICE.createSession(sessionRequest);

        return new ResponseEntity<>(convertToSessionResponse(session), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_SECRETARY", "ROLE_ORGANIZER", "ROLE_ADMINISTRATOR"})
    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SessionRequest sessionRequest
    ) throws NotFoundException {
        Session session = this.SERVICE.updateSession(sessionId, sessionRequest);

        return new ResponseEntity<>(convertToSessionResponse(session), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_SECRETARY", "ROLE_ORGANIZER", "ROLE_ADMINISTRATOR"})
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) throws NotFoundException {
        this.SERVICE.deleteSession(sessionId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_SECRETARY", "ROLE_ADMINISTRATOR"})
    @PutMapping("/{sessionId}/plan")
    public ResponseEntity<SessionResponse> planSession(
            @PathVariable UUID sessionId,
            @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) throws NotFoundException {
        Session session = this.SERVICE.planSession(sessionId, startDate, endDate);

        return new ResponseEntity<>(convertToSessionResponse(session), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @PutMapping("/{sessionId}/request")
    public ResponseEntity<SessionResponse> requestSessionToBePlanned(@PathVariable UUID sessionId) throws NotFoundException {
        Session session = this.SERVICE.requestSessionToBePlanned(sessionId);

        return new ResponseEntity<>(convertToSessionResponse(session), HttpStatus.OK);
    }
}
