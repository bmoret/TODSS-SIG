package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@PrimaryKeyJoinColumn(name = "session_id")
public class PhysicalSession extends Session {
    @Column(name = "location")
    private String address;

    public PhysicalSession() {
    }

    public PhysicalSession(SessionDetails details,
                           SessionState state,
                           SpecialInterestGroup sig,
                           List<Attendance> attendanceList,
                           List<Feedback> feedbackList,
                           String address
    ) {

        super(details, state, sig, attendanceList, feedbackList);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PhysicalSession that = (PhysicalSession) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}
