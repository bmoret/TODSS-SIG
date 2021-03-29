package com.snafu.todss.sig.sessies.domain;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

public class Attendance {
    @Id
    @GeneratedValue
    private UUID attendanceId;

    private boolean confirmed;
    private boolean absence;
    private boolean speaker;

//    @ManyToOne
//    @JoinColumn(name = "person_attendance", nullable = false)
//    private Person person;
//
//    @ManyToOne
//    @JoinColumn(name = "session_attendances_list", nullable = false)
//    private Session session;


    public Attendance() {}

    public Attendance(boolean confirmed, boolean absence, boolean speaker) {
        this.confirmed = confirmed;
        this.absence = absence;
        this.speaker = speaker;
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

    public UUID getAttendanceId() {
        return attendanceId;
    }

//    public Person getPerson() {
//        return person;
//    }
//
//    public Session getSession() {
//        return session;
//    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setAbsence(boolean absence) {
        this.absence = absence;
    }

    public void setSpeaker(boolean speaker) {
        this.speaker = speaker;
    }
}
