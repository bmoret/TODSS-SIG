package com.snafu.todss.sig.sessies.presentation.dto.response;

import java.util.UUID;

public class PersonCompactResponse {
    private final UUID supervisorId;
    private final String supervisorName;

    public PersonCompactResponse(UUID supervisorId, String supervisorName) {
        this.supervisorId = supervisorId;
        this.supervisorName = supervisorName;
    }

    public UUID getSupervisorId() {
        return supervisorId;
    }

    public String getSupervisorName() {
        return supervisorName;
    }
}
