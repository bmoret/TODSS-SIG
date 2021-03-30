package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.Session;

import java.util.UUID;

public class FeedbackResponse {
    private final UUID id;
    private final String description;
    private final Person person;
    private final Session session;

    public FeedbackResponse(UUID id, String description, Person person, Session session) {
        this.id = id;
        this.description = description;
        this.person = person;
        this.session = session;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Person getPerson() {
        return person;
    }

    public Session getSession() {
        return session;
    }
}
