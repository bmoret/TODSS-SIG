package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PhysicalSessionBuilder implements SessionBuilder {
    private SessionDetails details;
    private SessionState state;
    private SpecialInterestGroup sig;
    private Branch location;
    private String address;


    public PhysicalSessionBuilder() {
        this.details = new SessionDetails();
        this.state = SessionState.PLANNED;
        this.sig = null;
        this.location = null;
        this.address = "";
    }

    public PhysicalSessionBuilder setSig(SpecialInterestGroup sig) {
        this.sig = sig;
        return this;
    }

    public PhysicalSessionBuilder setStartDate(LocalDateTime startDate) {
        this.details.setStartDate(startDate);
        return this;
    }

    public PhysicalSessionBuilder setEndDate(LocalDateTime endDate) {
        this.details.setEndDate(endDate);
        return this;
    }

    public PhysicalSessionBuilder setSubject(String subject) {
        this.details.setSubject(subject);
        return this;
    }

    public PhysicalSessionBuilder setDescription(String description) {
        this.details.setDescription(description);
        return this;
    }

    public PhysicalSessionBuilder setState(SessionState state) {
        this.state = state;
        return this;
    }

    public PhysicalSessionBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public PhysicalSession build() {
        return new PhysicalSession(
                details,
                state,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address
        );
    }
}
