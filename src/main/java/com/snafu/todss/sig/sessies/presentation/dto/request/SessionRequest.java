package com.snafu.todss.sig.sessies.presentation.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionRequest {
    @NotNull
    public LocalDateTime startDate;

    @NotNull
    public LocalDateTime endDate;

    @NotBlank
    public String subject;

    @NotBlank
    public String description;

    @NotBlank
    public String location;

    @NotNull
    public Boolean isOnline;

    @NotNull
    public UUID sigId;
}
