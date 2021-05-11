package com.snafu.todss.sig.sessies.presentation.dto.request.session;

import com.snafu.todss.sig.sessies.domain.person.Person;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class OnlineSessionRequest extends SessionRequest{
    @NotBlank
    public String platform;

    @NotBlank
    public String joinUrl;

    public OnlineSessionRequest() {
    }

    public OnlineSessionRequest(
            @NotNull LocalDateTime startDate,
            @NotNull LocalDateTime endDate,
            @NotBlank String subject,
            @NotBlank String description,
            @NotNull UUID sigId,
            @NotBlank String platform,
            @NotBlank String joinUrl,
            String contactPerson
    ) {
        super(startDate, endDate, subject, description, sigId, contactPerson);
        this.platform = platform;
        this.joinUrl = joinUrl;
    }
}
