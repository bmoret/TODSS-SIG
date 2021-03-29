package com.snafu.todss.sig.sessies.presentation.dto.response;

import java.util.UUID;

public class AttendanceResponse {
    private final UUID attendanceId;
    private final boolean confirmed;
    private final boolean absence;
    private final boolean speaker;
//    private final Person person;
//    private final Session session;

    public AttendanceResponse(UUID attendanceId, boolean confirmed, boolean absence, boolean speaker//, Person person, Session session
    ) {
        this.attendanceId = attendanceId;
        this.confirmed = confirmed;
        this.absence = absence;
        this.speaker = speaker;
//        this.person = person.getName();
//        this.session = session.getId();
    }

    public UUID getAttendanceId() {
        return attendanceId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isAbsence() {
        return absence;
    }

    public boolean isSpeaker() {
        return speaker;
    }

//    public Person getPerson() {
//        return person;
//    }
//
//    public Session getSession() {
//        return session;
//    }
}
