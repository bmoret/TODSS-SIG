package com.snafu.todss.sig.sessies.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public class SpecialInterestGroupResponse {
    public UUID id;
    public String subject;
    public PersonCompactResponse manager;
    public List<PersonCompactResponse> organizers;

    public SpecialInterestGroupResponse() {
    }

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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setManager(PersonCompactResponse manager) {
        this.manager = manager;
    }

    public void setOrganizers(List<PersonCompactResponse> organizers) {
        this.organizers = organizers;
    }
}
