package com.snafu.todss.sig.sessies.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "sig")
public class SpecialInterestGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String subject;

    //private Person manager;

    //private List<Person> organizers;

    //private List<Session> sessions;

    public SpecialInterestGroup() {}
    public SpecialInterestGroup(
            String subject
    ) {
        this.subject = subject;
        //this.manager = manager;
        //this.organizers = organizers;
        //this.sessions = sessions;
    }

    public UUID getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    //public Session addSession(Session session) {this.sessions.add(session);}

    //public Session removeSession(UUID id) {this.sessions.stream().remove(where id = id);} <-- klopt nog niet
}
