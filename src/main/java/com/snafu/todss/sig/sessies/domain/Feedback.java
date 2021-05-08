package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Session session;

    @OneToOne
    private Person person;

    @Column
    private String description;

    public Feedback() {}
    public Feedback(String description, Session session, Person person) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Description of feedback cannot be empty.");
        }

        this.description = description;
        this.session = session;
        this.person = person;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Session getSession() {
        return session;
    }

    public Person getPerson() {
        return person;
    }
}
