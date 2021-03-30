package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.Session;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sig")
public class SpecialInterestGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String subject;

    @ManyToOne
    private Person manager;

    @ManyToMany
    private List<Person> organizers;

    @OneToMany(mappedBy = "sig")
    private List<Session> sessions;

    public SpecialInterestGroup() {}

    public SpecialInterestGroup(String subject, Person manager, List<Person> organizers, List<Session> sessions) {
        this.subject = subject;
        this.manager = manager;
        this.organizers = organizers;
        this.sessions = sessions;
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

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }

    public List<Person> getOrganizers() {
        return organizers;
    }

    public boolean addOrganizer(Person organizor) {
        if (this.organizers.contains(organizor)){
            return false;
        }
        return this.organizers.add(organizor);
    }

    public boolean removeOrganizer(Person organizor) {
        return this.organizers.remove(organizor);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public boolean addSession(Session session) {
        if (this.sessions.contains(session)){
            return false;
        }
        return this.sessions.add(session);
    }

    public boolean removeSession(Session session) {
        return this.sessions.remove(session);
    }
}
