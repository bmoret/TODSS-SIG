package com.snafu.todss.sig.security.application.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtCredentials {
    @Value("${JWT_SECRET}")
    protected String jwtSecret;

    @Value("${JWT_EXPIRATION_DATE_IN_MS}")
    protected Integer jwtExpirationInMs;

    @Value("${JWT_SECRET}")
    protected String jwtRefreshSecret;

    @Value("${JWT_EXPIRATION_DATE_IN_MS}")
    protected Integer jwtRefreshExpirationInMs;

    @Value("${JWT_AUDIENCE}")
    protected String jwtAudience;

    @Value("${JWT_ISSUER}")
    protected String jwtIssuer;
}
