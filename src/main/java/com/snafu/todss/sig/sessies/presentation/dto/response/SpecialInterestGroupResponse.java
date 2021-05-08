package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.person.Person;

import java.util.List;
import java.util.UUID;

public class SpecialInterestGroupResponse {
    public final UUID id;
    public final String subject;
    public final PersonCompactResponse manager;
    public final List<Person> organizers;

    public SpecialInterestGroupResponse(UUID id, String subject, PersonCompactResponse manager, List<Person> organizers) {
        this.id = id;
        this.subject = subject;
        this.manager = manager;
        this.organizers = organizers;
    }

    public UUID getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public PersonCompactResponse getManager() {
        return manager;
    }

    public List<Person> getOrganizers() {
        return organizers;
    }
}
