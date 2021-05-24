package com.snafu.todss.sig.security.presentation.dto.request;

import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Registration extends PersonRequest {
    @NotBlank
    public String username;

    @Size(min = 7)
    public String password;
}
