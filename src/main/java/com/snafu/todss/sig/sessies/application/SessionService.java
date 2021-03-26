package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.session.Session;
import com.snafu.todss.sig.sessies.domain.session.SessionFactory;
import com.snafu.todss.sig.sessies.presentation.dto.request.SessionRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<Session> getAllSessions() {
        return this.sessionRepository.findAll();
    }

    public Session getSessionById(UUID sessionId) throws NotFoundException {
        return this.sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("No session found with given id"));
    }

    public Session createSession(SessionRequest sessionRequest) {
        //Sig sig = sigService.findByName(sessionRequest.sig);
        Sig sig = null;
        return new SessionFactory()
                .setEndDate(sessionRequest.endDate)
                .setStartDate(sessionRequest.startDate)
                .setSubject(sessionRequest.subject)
                .setDescription(sessionRequest.description)
                .setLocation(sessionRequest.location)
                .setOnline(sessionRequest.isOnline)
                .setSig(sig)
                .build();
    }

    public Session updateSession(UUID sessionId, SessionRequest sessionRequest) throws NotFoundException {
        Session session = getSessionById(sessionId);
        session.getDetails().setStartDate(sessionRequest.startDate);
        session.getDetails().setEndDate(sessionRequest.endDate);
        session.getDetails().setSubject(sessionRequest.subject);
        session.getDetails().setDescription(sessionRequest.description);
        session.getDetails().setLocation(sessionRequest.location);
        session.getDetails().setOnline(sessionRequest.isOnline);
        return this.sessionRepository.save(session);
    }

    public void deleteSession(UUID sessionId) {
        this.sessionRepository.deleteById(sessionId);
    }
}
