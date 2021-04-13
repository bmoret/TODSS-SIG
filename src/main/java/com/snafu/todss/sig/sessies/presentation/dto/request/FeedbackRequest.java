package com.snafu.todss.sig.sessies.presentation.dto.request;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class FeedbackRequest {
    @NotNull
    public String description;

    @NotNull
    public UUID personId;

    @NotNull
    public UUID sessionId;
}
