package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.OnlineSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.PhysicalSessionRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;

public class SessionDirector {
    public static Session buildSession(SessionRequest request, SpecialInterestGroup sig) {
        if (PhysicalSessionRequest.class.isAssignableFrom(request.getClass())) {
            return buildPhysicalSession((PhysicalSessionRequest) request, sig);
        } else if (OnlineSessionRequest.class.isAssignableFrom(request.getClass())) {
            return buildOnlineSession((OnlineSessionRequest) request, sig);
        }
        throw new IllegalArgumentException("Not all input for a session was valid");
    }

    private static PhysicalSession buildPhysicalSession(PhysicalSessionRequest request, SpecialInterestGroup sig) {
        return new PhysicalSessionBuilder()
                .setEndDate(request.endDate)
                .setStartDate(request.startDate)
                .setSubject(request.subject)
                .setDescription(request.description)
                .setAddress(request.location)
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
}
