package com.snafu.todss.sig.sessies.presentation.dto.request.session;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class PhysicalSessionRequest extends SessionRequest{
    @NotBlank
    public String address;

    public PhysicalSessionRequest() {
    }

    public PhysicalSessionRequest(
            @NotNull LocalDateTime startDate,
            @NotNull LocalDateTime endDate,
            @NotBlank String subject,
            @NotBlank String description,
            @NotNull UUID sigId,
            @NotBlank String address,
            String contactPerson
    ) {
        super(startDate, endDate, subject, description, sigId, contactPerson);
        this.address = address;
    }
}
