package com.snafu.todss.sig.sessies.presentation.dto.request.attendance;

import javax.validation.constraints.NotNull;

public class PresenceRequest {
    @NotNull
    public Boolean isPresent;
}
