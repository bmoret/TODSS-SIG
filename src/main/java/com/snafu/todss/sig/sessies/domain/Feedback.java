package com.snafu.todss.sig.sessies.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

//    private Session session;
//    private Person person;

    @Column
    private String description;

    public Feedback() {}
    public Feedback(String description) {
        this.description = description;
        //this.session = session;
        //this.person = person;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
