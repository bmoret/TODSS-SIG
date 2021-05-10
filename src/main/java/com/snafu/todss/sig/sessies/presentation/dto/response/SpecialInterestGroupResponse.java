package com.snafu.todss.sig.sessies.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public class SpecialInterestGroupResponse {
    private UUID id;
    private String subject;
    private PersonCompactResponse manager;
    private List<PersonCompactResponse> organizers;

    public SpecialInterestGroupResponse() {
        //For Modelmapper to map domain class to this DTO
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

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public PersonCompactResponse getManager() {
        return manager;
    }

    public void setManager(PersonCompactResponse manager) {
        this.manager = manager;
    }

    public List<PersonCompactResponse> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(List<PersonCompactResponse> organizers) {
        this.organizers = organizers;
    }
}
