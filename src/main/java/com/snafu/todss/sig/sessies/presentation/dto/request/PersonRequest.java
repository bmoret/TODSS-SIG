package com.snafu.todss.sig.sessies.presentation.dto.request;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class PersonRequest {
    @NotNull
    public String email;

    @NotNull
    public String firstname;

    @NotNull
    public String lastname;

    @NotNull
    public String expertise;

    @NotNull
    public String branch;

    @NotNull
    public String role;

    @NotNull
    public String employedSince;

    @NotNull
    public UUID supervisorId;
}
