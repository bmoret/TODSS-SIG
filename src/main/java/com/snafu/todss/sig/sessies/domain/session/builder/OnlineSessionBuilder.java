package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OnlineSessionBuilder implements SessionBuilder {
    private static final String TEAMS = "TEAMS";

    private SessionDetails details;
    private SessionState state;
    private SpecialInterestGroup sig;
    private String platform;
    private String joinUrl;
    private Person contactPerson;


    public OnlineSessionBuilder() {
        this.details = new SessionDetails();
        this.state = SessionState.DRAFT;
        this.sig = null;
        this.platform = "";
        this.joinUrl = "";
        this.contactPerson = null;
    }

    @Override
    public OnlineSessionBuilder setSig(SpecialInterestGroup sig) {
        this.sig = sig;
        return this;
    }

    @Override
    public OnlineSessionBuilder setStartDate(LocalDateTime startDate) {
        this.details.setStartDate(startDate);
        return this;
    }

    @Override
    public OnlineSessionBuilder setEndDate(LocalDateTime endDate) {
        this.details.setEndDate(endDate);
        return this;
    }

    @Override
    public OnlineSessionBuilder setSubject(String subject) {
        this.details.setSubject(subject);
        return this;
    }

    @Override
    public OnlineSessionBuilder setDescription(String description) {
        this.details.setDescription(description);
        return this;
    }

    @Override
    public OnlineSessionBuilder setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
        return this;
    }

    public OnlineSessionBuilder setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public OnlineSessionBuilder setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
        return this;
    }

    @Override
    public OnlineSession build() {
        if (this.platform.equalsIgnoreCase(TEAMS)) {
            return new TeamsOnlineSession(
                    details,
                    state,
                    sig,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    joinUrl,
                    contactPerson
            );
        } else {
            return new OnlineSession(
                    details,
                    state,
                    sig,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    platform,
                    joinUrl,
                    contactPerson
            );
        }
    }
}
