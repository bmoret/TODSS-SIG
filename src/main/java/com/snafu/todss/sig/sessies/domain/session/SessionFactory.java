package com.snafu.todss.sig.sessies.domain.session;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SessionFactory {
    private SessionDetails details;
    private SessionState state;
    private SpecialInterestGroup sig;
    private List<Attendance> attendanceList;
    private List<Feedback> feedbackList;

    public SessionFactory() {
        this.details = new SessionDetails();
        this.state = SessionState.PLANNED;
        this.sig = null;
        this.attendanceList = new ArrayList<>();
        this.feedbackList = new ArrayList<>();
    }

    public SessionFactory setSig(Sig sig) {
        this.sig = sig;
        return this;
    }

    public SessionFactory setAttendance(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
        return this;
    }

    public SessionFactory setFeedback(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
        return this;
    }

    public SessionFactory setStartDate(LocalDateTime startDate) {
        this.details.setStartDate(startDate);
        return this;
    }

    public SessionFactory setEndDate(LocalDateTime endDate) {
        this.details.setEndDate(endDate);
        return this;
    }

    public SessionFactory setSubject(String subject) {
        this.details.setSubject(subject);
        return this;
    }

    public SessionFactory setDescription(String description) {
        this.details.setDescription(description);
        return this;
    }

    public SessionFactory setLocation(String location) {
        this.details.setLocation(location);
        return this;
    }

    public SessionFactory setOnline(Boolean online) {
        this.details.setOnline(online);
        return this;
    }

    public Session build() {
        return new Session(
                details,
                state,
                sig
        );
    }
}
