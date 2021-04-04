package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import java.util.List;

public class OnlineSession extends Session {
    private String platform;
    private String joinUrl;

    public OnlineSession() {
    }

    public OnlineSession(
            SessionDetails details,
            SessionState state,
            SpecialInterestGroup sig,
            List<Attendance> attendanceList,
            List<Feedback> feedbackList,
            String platform,
            String joinUrl
    ) {
        super(details, state, sig, attendanceList, feedbackList);
        this.platform = platform;
        this.joinUrl = joinUrl;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getJoinURL() {
        return this.joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }
}
