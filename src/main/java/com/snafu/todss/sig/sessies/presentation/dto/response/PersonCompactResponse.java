package com.snafu.todss.sig.sessies.presentation.dto.response;

import java.util.UUID;

public class PersonCompactResponse {
    private UUID personId;
    private String personName;

    public PersonCompactResponse() {
    }

    public PersonCompactResponse(UUID personId, String personName) {
        this.personId = personId;
        this.personName = personName;
    }

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
