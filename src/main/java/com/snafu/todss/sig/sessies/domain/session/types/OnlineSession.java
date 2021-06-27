package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "online_session")
@PrimaryKeyJoinColumn(name = "session_id")
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
            String joinUrl,
            Person contactPerson
    ) {
        super(details, state, sig, attendanceList, feedbackList, contactPerson);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OnlineSession that = (OnlineSession) o;
        return Objects.equals(platform, that.platform) &&
                Objects.equals(joinUrl, that.joinUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), platform, joinUrl);
    }
}
