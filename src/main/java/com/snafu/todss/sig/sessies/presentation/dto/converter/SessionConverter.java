package com.snafu.todss.sig.sessies.presentation.dto.converter;

import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.response.SessionResponse;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class SessionConverter {
    private SessionConverter() {
    }

    public static List<SessionResponse> convertSessionListToResponse(List<Session> sessions) {
        return sessions.stream()
                .map(SessionConverter::convertSessionToResponse)
                .collect(Collectors.toList());
    }

    public static SessionResponse convertSessionToResponse(Session session) {
        SessionResponse response = new ModelMapper().map(session, SessionResponse.class);
        return setSessionType(session, response);
    }

    private static SessionResponse setSessionType(Session session, SessionResponse response) {
        if (session instanceof TeamsOnlineSession) response.setType("TEAMS");
        else if (session instanceof OnlineSession) response.setType("ONLINE");
        else if (session instanceof PhysicalSession) response.setType("PHYSICAL");
        else response.setType("UNKNOWN");
        return response;
    }
}
