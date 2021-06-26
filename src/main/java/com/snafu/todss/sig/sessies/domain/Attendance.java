package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue
    private UUID id;

    private AttendanceState state;

    private boolean isSpeaker;

    @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Person person;

    @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Session session;

    public static Attendance of(Person person, Session session) {
        return new Attendance(AttendanceState.NO_SHOW, false, person, session);
    }

    public Attendance() {}

    public Attendance(AttendanceState state, boolean isSpeaker, Person person, Session session) {
        this.state = state;
        this.isSpeaker = isSpeaker;
        this.person = person;
        this.session = session;
    }

    public UUID getId() {
        return id;
    }

    public AttendanceState getState() {
        return state;
    }

    public void setState(AttendanceState state) {
        this.state = state;
    }

    public boolean isSpeaker() {
        return isSpeaker;
    }

    public void setSpeaker(boolean speaker) {
        isSpeaker = speaker;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
