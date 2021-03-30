package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import java.util.UUID;

public class SessionResponse {
    private final UUID id;
    private final SessionState state;
    private final SessionDetails details;

    public SessionResponse(UUID id, SessionState state, SessionDetails details) {
        this.id = id;
        this.state = state;
        this.details = details;
    }

    public UUID getId() {
        return id;
    }

    public SessionState getState() {
        return state;
    }

    public SessionDetails getDetails() {
        return details;
    }
}
