package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
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

    public static Session build(SessionRequest request, SpecialInterestGroup sig, Person contactPerson) {
        inputNotNull(request);
        if (PhysicalSessionRequest.class.isAssignableFrom(request.getClass())) {
            return buildPhysicalSession((PhysicalSessionRequest) request, sig, contactPerson);
        } else if (OnlineSessionRequest.class.isAssignableFrom(request.getClass())) {
            return buildOnlineSession((OnlineSessionRequest) request, sig, contactPerson);
        }
        throw new IllegalArgumentException("Cannot create session");
    }

    private static PhysicalSession buildPhysicalSession(PhysicalSessionRequest request, SpecialInterestGroup sig, Person contactPerson) {
        return new PhysicalSessionBuilder()
                .setEndDate(request.endDate)
                .setStartDate(request.startDate)
                .setSubject(request.subject)
                .setDescription(request.description)
                .setAddress(request.address)
                .setSig(sig)
                .setContactPerson(contactPerson)
                .build();
    }

    private static OnlineSession buildOnlineSession(OnlineSessionRequest request, SpecialInterestGroup sig, Person contactPerson) {
        return new OnlineSessionBuilder()
                .setEndDate(request.endDate)
                .setStartDate(request.startDate)
                .setSubject(request.subject)
                .setDescription(request.description)
                .setPlatform(request.platform)
                .setJoinUrl(request.joinUrl)
                .setSig(sig)
                .setContactPerson(contactPerson)
                .build();
    }

    public static Session rebuild(Session session, SessionRequest request, SpecialInterestGroup sig, Person contactPerson) {
        System.out.println("rebuild");
        inputNotNull(request);
        if (PhysicalSessionRequest.class.isAssignableFrom(request.getClass())){
            System.out.println("Physical");
            Session physicalSession = buildPhysicalSession((PhysicalSessionRequest) request, sig, contactPerson);
            physicalSession.setId(session.getId());
            physicalSession.addAllAttendees(session.getAttendances());
            physicalSession.addAllFeedback(session.getFeedback());
            return physicalSession;
        } else if (OnlineSessionRequest.class.isAssignableFrom(request.getClass())) {
            System.out.println("Online");
            Session onlineSession = buildOnlineSession((OnlineSessionRequest) request, sig, contactPerson);
            onlineSession.setId(session.getId());
            onlineSession.addAllAttendees(session.getAttendances());
            onlineSession.addAllFeedback(session.getFeedback());
            return onlineSession;
        }
        throw new IllegalArgumentException("Cannot recreate session");
    }

    public static Session update(Session session, SessionRequest request, SpecialInterestGroup sig, Person contactPerson) {
        if (PhysicalSessionRequest.class.isAssignableFrom(request.getClass())) {
            if (!PhysicalSession.class.isAssignableFrom(session.getClass())){
                return rebuild(session, request, sig, contactPerson);
            }
            return updatePhysicalSession((PhysicalSession) session, (PhysicalSessionRequest) request, sig, contactPerson);
        } else if (OnlineSessionRequest.class.isAssignableFrom(request.getClass())) {
            System.out.println(!OnlineSession.class.isAssignableFrom(session.getClass()));
            if (!OnlineSession.class.isAssignableFrom(session.getClass())){
                return rebuild(session, request, sig, contactPerson);
            }
            return updateOnlineSession((OnlineSession) session, (OnlineSessionRequest) request, sig, contactPerson);
        }
        throw new IllegalArgumentException("Cannot update session");
    }

    private static Session updatePhysicalSession(PhysicalSession session, PhysicalSessionRequest request, SpecialInterestGroup sig, Person contactPerson) {
        SessionDetails details = session.getDetails();
        details.setStartDate(request.startDate);
        details.setEndDate(request.endDate);
        details.setSubject(request.subject);
        details.setDescription(request.description);
        session.setSig(sig);
        session.setAddress(request.address);
        session.setContactPerson(contactPerson);
        return session;
    }

    private static Session updateOnlineSession(OnlineSession session, OnlineSessionRequest request, SpecialInterestGroup sig, Person contactPerson) {
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
        session.setContactPerson(contactPerson);
        return session;
    }
}
