package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import java.util.UUID;

public class SessionResponse {
    public UUID id;
    public SessionState state;
    public SessionDetails details;
}
