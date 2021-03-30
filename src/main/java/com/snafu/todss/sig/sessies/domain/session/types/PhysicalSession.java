package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
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

}
