package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PhysicalSessionBuilder implements SessionBuilder {
    private SessionDetails details;
    private SessionState state;
    private SpecialInterestGroup sig;
    private String address;
    private Person contactPerson;

    public PhysicalSessionBuilder() {
        this.details = new SessionDetails();
        this.state = SessionState.DRAFT;
        this.sig = null;
        this.address = "";
        this.contactPerson = null;
    }

    @Override
    public PhysicalSessionBuilder setSig(SpecialInterestGroup sig) {
        this.sig = sig;
        return this;
    }

    @Override
    public PhysicalSessionBuilder setStartDate(LocalDateTime startDate) {
        this.details.setStartDate(startDate);
        return this;
    }

    @Override
    public PhysicalSessionBuilder setEndDate(LocalDateTime endDate) {
        this.details.setEndDate(endDate);
        return this;
    }

    @Override
    public PhysicalSessionBuilder setSubject(String subject) {
        this.details.setSubject(subject);
        return this;
    }

    @Override
    public PhysicalSessionBuilder setDescription(String description) {
        this.details.setDescription(description);
        return this;
    }

    public PhysicalSessionBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public PhysicalSessionBuilder setContactPerson(Person person) {
        this.contactPerson = person;

        return this;
    }

    @Override
    public PhysicalSession build() {
        return new PhysicalSession(
                details,
                state,
                sig,
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                contactPerson
        );
    }
}
