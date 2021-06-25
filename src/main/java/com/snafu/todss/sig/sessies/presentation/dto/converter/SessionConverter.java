package com.snafu.todss.sig.sessies.presentation.dto.converter;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonCompactResponse;
import com.snafu.todss.sig.sessies.presentation.dto.response.SessionResponse;
import com.snafu.todss.sig.sessies.presentation.dto.response.SpecialInterestGroupResponse;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class SessionConverter {
    private SessionConverter() {
    }

    public static List<SessionResponse> convertSessionListToResponse(List<Session> sessions) {
        return sessions.stream().map(session -> {
            SessionResponse res = convertSessionToResponse(session);
            res.setSpecialInterestGroup(session.getSig() == null? null : convertSigToSigResponse(session.getSig()));
            return res;
        }).collect(Collectors.toList());
    }

    private static SpecialInterestGroupResponse convertSigToSigResponse(SpecialInterestGroup sig) {
        PersonCompactResponse manager = sig.getManager() == null? null :  convertPersonToResponse(sig.getManager());
        List<PersonCompactResponse> organizers = sig.getOrganizers() == null? null : convertPersonToListResponse(sig.getOrganizers());
        return new SpecialInterestGroupResponse(
                sig.getId(),
                sig.getSubject(),
                manager,
                organizers
        );
    }

    private static List<PersonCompactResponse> convertPersonToListResponse(List<Person> people) {
        return people.stream().map(SessionConverter::convertPersonToResponse).collect(Collectors.toList());
    }

    private static PersonCompactResponse convertPersonToResponse(Person person) {
        return new PersonCompactResponse(
                person.getId(),
                person.getDetails().getLastname() + ", " + person.getDetails().getFirstname()
        );
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
