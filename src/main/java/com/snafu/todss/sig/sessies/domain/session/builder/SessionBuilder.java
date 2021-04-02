package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.Session;

import java.time.LocalDateTime;


public interface SessionBuilder{
    SessionBuilder setSig(SpecialInterestGroup sig);
    SessionBuilder setStartDate(LocalDateTime startDate);
    SessionBuilder setEndDate(LocalDateTime endDate);
    SessionBuilder setSubject(String subject);
    SessionBuilder setDescription(String description);
    SessionBuilder setState(SessionState state);
   Session build();
}
