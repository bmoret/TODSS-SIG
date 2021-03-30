package com.snafu.todss.sig.sessies.presentation.dto.request.session;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OnlineSessionRequest.class, name = "ONLINE_SESSION_REQUEST"),
        @JsonSubTypes.Type(value = PhysicalSessionRequest.class, name = "PHYSICAL_SESSION_REQUEST")
})
public class SessionRequest {
    @NotNull
    public LocalDateTime startDate;

    @NotNull
    public LocalDateTime endDate;

    @NotBlank
    public String subject;

    @NotBlank
    public String description;

    @NotNull
    public UUID sigId;
}
