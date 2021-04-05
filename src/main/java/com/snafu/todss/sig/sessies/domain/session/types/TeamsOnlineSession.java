package com.snafu.todss.sig.sessies.domain.session.types;


import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import java.util.List;

public class TeamsOnlineSession extends OnlineSession {
    private static final String PLATFORM = "Teams";

    public TeamsOnlineSession() {
    }

    public  TeamsOnlineSession(SessionDetails details,
                               SessionState state,
                               SpecialInterestGroup sig,
                               List<Attendance> attendanceList,
                               List<Feedback> feedbackList,
                               String joinUrl
    ) {
        super(details, state, sig, attendanceList, feedbackList, PLATFORM, joinUrl);
    }

    @Override
    public String getPlatform() {
        return PLATFORM;
    }

    @Override
    public void setPlatform(String platform) {
    }
}
