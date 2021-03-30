package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.session.Session;

import java.util.UUID;

public class AttendanceResponse {
    private final UUID attendanceId;
    private final boolean isConfirmed;
    private final boolean isAbsent;
    private final boolean isSpeaker;
    private final PersonCompactResponse person;
    private final UUID sessionId;

    public AttendanceResponse(UUID attendanceId,
                              boolean isConfirmed,
                              boolean isAbsent,
                              boolean isSpeaker,
                              Person person,
                              Session session
    ) {
        this.attendanceId = attendanceId;
        this.isConfirmed = isConfirmed;
        this.isAbsent = isAbsent;
        this.isSpeaker = isSpeaker;
        PersonDetails details = person.getDetails();
        this.person = new PersonCompactResponse(
                person.getId(),
                String.format("%s, %s",details.getLastname(), details.getFirstname() )
        );
        this.sessionId = session.getId();
    }

    public UUID getAttendanceId() {
        return attendanceId;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public boolean isAbsent() {
        return isAbsent;
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
