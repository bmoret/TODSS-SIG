package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;

import java.time.LocalDateTime;


public interface SessionBuilder{
    SessionBuilder setSig(SpecialInterestGroup sig);
    SessionBuilder setStartDate(LocalDateTime startDate);
    SessionBuilder setEndDate(LocalDateTime endDate);
    SessionBuilder setSubject(String subject);
    SessionBuilder setDescription(String description);
    SessionBuilder setContactPerson(Person contactPerson);
    Session build();
}
