package com.snafu.todss.sig.sessies.domain.session;

import com.snafu.todss.sig.sessies.domain.session.types.Session;

import javax.persistence.PostLoad;
import java.time.LocalDateTime;

public class SessionListener {
    @PostLoad
    public void checkOnLoad(Session session) {
        if (session.getDetails() == null) return;
        checkForOngoingSession(session);
        checkForEndedSession(session);
    }

    private void checkForOngoingSession(Session session) {
        LocalDateTime startDate = session.getDetails().getStartDate();
        if (startDate == null) return;
        if (session.getState().equals(SessionState.PLANNED) && startDate.isBefore(LocalDateTime.now())){
            session.nextState();
        }
    }

    private void checkForEndedSession(Session session) {
        LocalDateTime endDate = session.getDetails().getEndDate();
        if (endDate == null) return;
        if (session.getState().equals(SessionState.ONGOING) && endDate.isBefore(LocalDateTime.now())){
            session.nextState();
        }
    }
}
