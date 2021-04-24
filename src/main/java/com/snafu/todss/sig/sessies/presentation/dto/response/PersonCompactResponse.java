package com.snafu.todss.sig.sessies.presentation.dto.response;

import java.util.UUID;

public class PersonCompactResponse {
    private final UUID personId;
    private final String personName;

    public PersonCompactResponse(UUID personId, String personName) {
        this.personId = personId;
        this.personName = personName;
    }

    public UUID getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }
}
