package com.snafu.todss.sig.sessies.presentation.dto.request.session;

import javax.validation.constraints.NotBlank;

public class OnlineSessionRequest extends SessionRequest{
    @NotBlank
    public String platform;

    @NotBlank
    public String joinUrl;
}
