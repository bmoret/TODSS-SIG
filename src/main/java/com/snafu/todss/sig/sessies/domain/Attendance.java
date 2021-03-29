package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.Session;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue
    private UUID id;

    //todo: isConfirmed en isAbsent vervangen met isPresent
    // (aanmelden is attendance object aanmaken, komen is isPresent = true absent is false)
    private boolean isConfirmed;
    private boolean isAbsent;
    private boolean isSpeaker;

    @ManyToOne(optional = false)
    private Person person;

    @ManyToOne(optional = false)
    private Session session;

    public static Attendance of(Person person, Session session) {
        return new Attendance(false, false, false, person, session);
    }

    public Attendance() {}

    public Attendance(boolean isConfirmed, boolean isAbsent, boolean speaker, Person person, Session session) {
        this.isConfirmed = isConfirmed;
        this.isAbsent = isAbsent;
        this.isSpeaker = speaker;
        this.person = person;
        this.session = session;
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

    public UUID getAttendanceId() {
        return this.id;
    }

    public Person getPerson() {
        return person;
    }

    public Session getSession() {
        return session;
    }

    public void setConfirmed(boolean confirmed) {
        this.isConfirmed = confirmed;
    }

    public void setAbsent(boolean absent) {
        isAbsent = absent;
    }

    public void setSpeaker(boolean speaker) {
        isSpeaker = speaker;
    }
}
