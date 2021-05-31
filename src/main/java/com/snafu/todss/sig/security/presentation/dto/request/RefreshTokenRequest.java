package com.snafu.todss.sig.security.presentation.dto.request;

import javax.validation.constraints.NotBlank;

public class RefreshTokenRequest {
    @NotBlank
    public String refreshToken;

    @NotBlank
    public String accessToken;
}