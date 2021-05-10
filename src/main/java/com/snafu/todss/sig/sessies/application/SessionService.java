package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.builder.SessionDirector;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SessionService {
    private final SessionRepository SESSION_REPOSITORY;
    private final SpecialInterestGroupService SIG_SERVICE;
    private final PersonService personService;

    public SessionService(SessionRepository sessionRepository, SpecialInterestGroupService sigService, PersonService personService) {
        this.SESSION_REPOSITORY = sessionRepository;
        this.SIG_SERVICE = sigService;
        this.personService = personService;
    }

    public List<Session> getAllSessions() {
        return this.SESSION_REPOSITORY.findAll();
    }

    public Session getSessionById(UUID sessionId) throws NotFoundException {
        return this.SESSION_REPOSITORY.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("No session found with given id"));
    }

    public Session createSession(SessionRequest sessionRequest) throws NotFoundException {
        SpecialInterestGroup sig = this.SIG_SERVICE.getSpecialInterestGroupById(sessionRequest.sigId);
        Person person = null;
        if (sessionRequest.contactPerson != null) {
            person = personService
                    .getPerson(sessionRequest.contactPerson);
        }
        Session session = SessionDirector.build(sessionRequest, sig, person);
        return this.SESSION_REPOSITORY.save(session);
    }

    public Session updateSession(UUID sessionId, SessionRequest sessionRequest) throws NotFoundException {
        Session session = getSessionById(sessionId);
        Person person = null;
        if (sessionRequest.contactPerson != null) {
            person = personService
                    .getPerson(sessionRequest.contactPerson);
        }
        SpecialInterestGroup sig = this.SIG_SERVICE.getSpecialInterestGroupById(sessionRequest.sigId);
        session = SessionDirector.update(session, sessionRequest, sig, person);
        return this.SESSION_REPOSITORY.save(session);
    }

    public void deleteSession(UUID sessionId) throws NotFoundException {
        if (!this.SESSION_REPOSITORY.existsById(sessionId)){
            throw new NotFoundException("No session found with given id");
        }
        this.SESSION_REPOSITORY.deleteById(sessionId);
    }

    public Session planSession(UUID sessionId, LocalDateTime startDate, LocalDateTime endDate) throws NotFoundException {
        Session session = getSessionById(sessionId);
        if (session.getState() != SessionState.TO_BE_PLANNED) {
            throw new IllegalStateException("Session can only be planned if session state is TO_BE_PLANNED");
        }
        if (startDate.isBefore(LocalDateTime.now()) || endDate.isBefore(LocalDateTime.now()) ) {
            throw new IllegalArgumentException("Dates must be after now");
        }
        checkForSessionStartBeforeEnd(startDate, endDate);
        checkForSessionDuration(startDate, endDate);

        session.getDetails().setStartDate(startDate);
        session.getDetails().setEndDate(endDate);
        session.nextState();
        return SESSION_REPOSITORY.save(session);
    }

    private void checkForSessionStartBeforeEnd(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null &&
                endDate != null &&
                startDate.isAfter(endDate)
        ) {
            throw new IllegalArgumentException("Start date must come before the end date");
        }
    }

    private void checkForSessionDuration(LocalDateTime startDate, LocalDateTime endDate) {
        final int MAXIMUM_SESSION_LENGTH_IN_MS = 604800000;
        if ( endDate != null &&
                startDate != null &&
                Math.abs(Duration.between(endDate, startDate).toMillis()) > MAXIMUM_SESSION_LENGTH_IN_MS
        ) {
            throw new IllegalArgumentException(String.format("Session duration cannot be longer than %s milliseconds", MAXIMUM_SESSION_LENGTH_IN_MS));
        }
    }







    public Session requestSessionToBePlanned(UUID sessionId) throws NotFoundException {
        Session session = getSessionById(sessionId);
        if (session.getState() != SessionState.DRAFT) {
            throw new IllegalStateException("Session can only be requested for planning as a draft");
        }
        session.nextState();
        return session;
    }
}
