package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.SessionService;
import com.snafu.todss.sig.sessies.domain.session.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.SessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.SessionResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    private SessionResponse convertSessionToResponse(Session session) {
        SessionResponse response = new SessionResponse();
        response.details = session.getDetails();
        response.state = session.getState();
        response.id = session.getId();
        return response;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        List<Session> sessions = this.sessionService.getAllSessions();
        List<SessionResponse> responses = new ArrayList<>();
        for (Session session : sessions) {
            responses.add(convertSessionToResponse(session));
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable UUID sessionId) throws NotFoundException {
        Session session = this.sessionService.getSessionById(sessionId);
        return new ResponseEntity<>(convertSessionToResponse(session), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest sessionRequest) throws NotFoundException {
        Session session = this.sessionService.createSession(sessionRequest);
        return new ResponseEntity<>(convertSessionToResponse(session), HttpStatus.OK);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SessionRequest sessionRequest
    ) throws NotFoundException {
        Session session = this.sessionService.updateSession(sessionId, sessionRequest);
        return new ResponseEntity<>(convertSessionToResponse(session), HttpStatus.OK);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) throws NotFoundException {
        this.sessionService.deleteSession(sessionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
