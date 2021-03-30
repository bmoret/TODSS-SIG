package com.snafu.todss.sig.sessies.domain.session.types;


import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import java.util.ArrayList;
import java.util.List;

public class TeamsOnlineSession extends OnlineSession {
    private static final String PLATFORM = "Teams";

    private String joinURL;

    public TeamsOnlineSession(String joinURL) {
        this.joinURL = joinURL;
    }

    public  TeamsOnlineSession(SessionDetails details,
                               SessionState state,
                               SpecialInterestGroup sig,
                               List<Attendance> attendanceList,
                               List<Feedback> feedbackList,
                               String joinUrl) {
        super(details, state, sig, attendanceList, feedbackList, PLATFORM, joinUrl);
    }

    @Override
    public String getPlatform() {
        return PLATFORM;
    }

    @Override
    public String getJoinURL() {
        return this.joinURL;
    }
}
