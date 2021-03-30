package com.snafu.todss.sig.sessies.presentation.dto.request.session;

import javax.validation.constraints.NotBlank;


public class PhysicalSessionRequest extends SessionRequest{
    @NotBlank
    public String location;

    @NotBlank
    public String address;
}
