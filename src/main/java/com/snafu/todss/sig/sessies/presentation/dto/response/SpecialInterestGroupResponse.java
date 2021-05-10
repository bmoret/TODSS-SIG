package com.snafu.todss.sig.sessies.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public class SpecialInterestGroupResponse {
    public final UUID id;
    public final String subject;
    public final PersonCompactResponse manager;
    public final List<PersonCompactResponse> organizers;

    public SpecialInterestGroupResponse(UUID id, String subject, PersonCompactResponse manager, List<PersonCompactResponse> organizers) {
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

    public List<PersonCompactResponse> getOrganizers() {
        return organizers;
    }
}
