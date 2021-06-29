package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "teams_online_session")
@PrimaryKeyJoinColumn(name = "online_session_id")
public class TeamsOnlineSession extends OnlineSession {
    private static final String PLATFORM = "Teams";

    public TeamsOnlineSession() {
    }

    public  TeamsOnlineSession(SessionDetails details,
                               SessionState state,
                               SpecialInterestGroup sig,
                               List<Attendance> attendanceList,
                               List<Feedback> feedbackList,
                               String joinUrl,
                               Person contactPerson
    ) {
        super(details, state, sig, attendanceList, feedbackList, PLATFORM, joinUrl, contactPerson);
    }

    @Override
    public String getPlatform() {
        return PLATFORM;
    }

    @Override
    public void setPlatform(String platform) {
        throw new UnsupportedOperationException("Not allowed to change platform of a Teams online session");
    }
}
