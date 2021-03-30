package com.snafu.todss.sig.sessies.domain.session;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SessionBuilder {
    private SessionDetails details;
    private SessionState state;
    private SpecialInterestGroup sig;

    public SessionBuilder() {
        this.details = new SessionDetails();
        this.state = SessionState.PLANNED;
        this.sig = null;
    }

    public SessionBuilder setSig(SpecialInterestGroup sig) {
        this.sig = sig;
        return this;
    }

    public SessionBuilder setStartDate(LocalDateTime startDate) {
        this.details.setStartDate(startDate);
        return this;
    }

    public SessionBuilder setEndDate(LocalDateTime endDate) {
        this.details.setEndDate(endDate);
        return this;
    }

    public SessionBuilder setSubject(String subject) {
        this.details.setSubject(subject);
        return this;
    }

    public SessionBuilder setDescription(String description) {
        this.details.setDescription(description);
        return this;
    }

    public SessionBuilder setLocation(String location) {
        this.details.setLocation(location);
        return this;
    }

    public SessionBuilder setOnline(Boolean online) {
        this.details.setOnline(online);
        return this;
    }

    public Session build() {
        return new Session(
                details,
                state,
                sig,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
