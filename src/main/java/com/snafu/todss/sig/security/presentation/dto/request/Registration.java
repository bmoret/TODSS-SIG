package com.snafu.todss.sig.security.presentation.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Registration {
    @NotBlank
    public String username;

    @Size(min = 7)
    public String password;
}
