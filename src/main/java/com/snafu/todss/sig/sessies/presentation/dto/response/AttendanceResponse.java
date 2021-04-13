package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.StateAttendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.session.types.Session;

import java.util.UUID;

public class AttendanceResponse {
    private final UUID id;
    private final StateAttendance state;
    private final boolean isSpeaker;
    private final PersonCompactResponse person;
    private final UUID sessionId;

    public AttendanceResponse(UUID id,
                              StateAttendance state,
                              boolean isSpeaker,
                              Person person,
                              Session session
    ) {
        this.id = id;
        this.state = state;
        this.isSpeaker = isSpeaker;
        PersonDetails details = person.getDetails();
        this.person = new PersonCompactResponse(
                person.getId(),
                String.format("%s, %s",details.getLastname(), details.getFirstname() )
        );
        this.sessionId = session.getId();
    }

    public UUID getId() {
        return id;
    }

    public StateAttendance getState() {
        return state;
    }

    public boolean isSpeaker() {
        return isSpeaker;
    }

    public PersonCompactResponse getPerson() {
        return person;
    }

    public UUID getSession() {
        return sessionId;
    }
}
