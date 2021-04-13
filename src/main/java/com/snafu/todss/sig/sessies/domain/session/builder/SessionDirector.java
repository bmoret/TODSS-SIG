package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.OnlineSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import static com.snafu.todss.sig.sessies.util.InputValidations.inputNotNull;

public class SessionDirector {
    private SessionDirector() {
    }

    public static Session build(SessionRequest request, SpecialInterestGroup sig) {
        inputNotNull(request);
        if (PhysicalSessionRequest.class.isAssignableFrom(request.getClass())) {
            return buildPhysicalSession((PhysicalSessionRequest) request, sig);
        } else if (OnlineSessionRequest.class.isAssignableFrom(request.getClass())) {
            return buildOnlineSession((OnlineSessionRequest) request, sig);
        }
        throw new IllegalArgumentException("Cannot create session");
    }

    private static PhysicalSession buildPhysicalSession(PhysicalSessionRequest request, SpecialInterestGroup sig) {
        return new PhysicalSessionBuilder()
                .setEndDate(request.endDate)
                .setStartDate(request.startDate)
                .setSubject(request.subject)
                .setDescription(request.description)
                .setAddress(request.address)
                .setSig(sig)
                .build();
    }

    private static OnlineSession buildOnlineSession(OnlineSessionRequest request, SpecialInterestGroup sig) {
        return new OnlineSessionBuilder()
                .setEndDate(request.endDate)
                .setStartDate(request.startDate)
                .setSubject(request.subject)
                .setDescription(request.description)
                .setPlatform(request.platform)
                .setJoinUrl(request.joinUrl)
                .setSig(sig)
                .build();
    }

    public static Session update(Session session, SessionRequest request, SpecialInterestGroup sig) {
        if (PhysicalSessionRequest.class.isAssignableFrom(request.getClass())) {
            return updatePhysicalSession((PhysicalSession) session, (PhysicalSessionRequest) request, sig);
        } else if (OnlineSessionRequest.class.isAssignableFrom(request.getClass())) {
            return updateOnlineSession((OnlineSession) session, (OnlineSessionRequest) request, sig);
        }
        throw new IllegalArgumentException("Cannot update session");
    }

    private static Session updatePhysicalSession(PhysicalSession session, PhysicalSessionRequest request, SpecialInterestGroup sig) {
        SessionDetails details = session.getDetails();
        details.setStartDate(request.startDate);
        details.setEndDate(request.endDate);
        details.setSubject(request.subject);
        details.setDescription(request.description);
        session.setSig(sig);
        session.setAddress(request.address);
        return session;
    }

    private static Session updateOnlineSession(OnlineSession session, OnlineSessionRequest request, SpecialInterestGroup sig) {
        SessionDetails details = session.getDetails();
        details.setStartDate(request.startDate);
        details.setEndDate(request.endDate);
        details.setSubject(request.subject);
        details.setDescription(request.description);
        session.setSig(sig);
        session.setJoinUrl(request.joinUrl);
        if (!session.getPlatform().equalsIgnoreCase("Teams")) {
            session.setPlatform(request.platform);
        }
        return session;
    }
}
