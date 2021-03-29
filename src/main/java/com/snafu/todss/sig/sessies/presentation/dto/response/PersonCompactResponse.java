package com.snafu.todss.sig.sessies.presentation.dto.response;

public class PersonCompactResponse {
    private final Long supervisorId;
    private final String supervisorName;

    public PersonCompactResponse(Long supervisorId, String supervisorName) {
        this.supervisorId = supervisorId;
        this.supervisorName = supervisorName;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public String getSupervisorName() {
        return supervisorName;
    }
}
