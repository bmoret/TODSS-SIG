package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.SessionService;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.SessionResponse;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class
SessionController {
    private final SessionService SERVICE;

    public SessionController(SessionService sessionService) {
        this.SERVICE = sessionService;
    }

    private SessionResponse convertSessionToResponse(Session session) {
        SessionResponse response = new ModelMapper().map(session, SessionResponse.class);
        return setSessionType(session, response);
    }

    private SessionResponse setSessionType(Session session, SessionResponse response) {
        if (session instanceof TeamsOnlineSession) response.setType("TAMS");
        else if (session instanceof OnlineSession) response.setType("ONLINE");
        else if (session instanceof PhysicalSession) response.setType("PHYSICAL");
        return response;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        List<Session> sessions = this.SERVICE.getAllSessions();
        List<SessionResponse> responses = new ArrayList<>();
        for (Session session : sessions) {
            responses.add(convertSessionToResponse(session));
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable UUID sessionId) throws NotFoundException {
        Session session = this.SERVICE.getSessionById(sessionId);

        return new ResponseEntity<>(convertSessionToResponse(session), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest sessionRequest) throws NotFoundException {
        Session session = this.SERVICE.createSession(sessionRequest);

        return new ResponseEntity<>(convertSessionToResponse(session), HttpStatus.OK);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SessionRequest sessionRequest
    ) throws NotFoundException {
        Session session = this.SERVICE.updateSession(sessionId, sessionRequest);

        return new ResponseEntity<>(convertSessionToResponse(session), HttpStatus.OK);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) throws NotFoundException {
        this.SERVICE.deleteSession(sessionId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
