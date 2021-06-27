package com.snafu.todss.sig.security.application.util;


import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {
    private final JwtCredentials CREDENTIALS;

    public JwtValidator(JwtCredentials CREDENTIALS) {
        this.CREDENTIALS = CREDENTIALS;
    }

    public void validateAccessJwt(String jwt) {
        validateJwt(jwt, CREDENTIALS.jwtSecret);
    }

    public void validateRefreshJwt(String jwt) {
        validateJwt(jwt, CREDENTIALS.jwtRefreshSecret);
    }

    public void validateJwt(String jwt, String secret) {
        if (jwt == null || jwt.isEmpty()) throw new JwtException("Empty JWT");
        JwtParser jwtParser = createParser(secret);
        tryToParseClaimsFromToken(jwtParser, jwt);
    }

    private JwtParser createParser(String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build();
    }

    private Jws<Claims> tryToParseClaimsFromToken(JwtParser parser, String jwt) {
        Jws<Claims> parsedToken = null;
        try {
            parsedToken = parser.parseClaimsJws(jwt);
            validateTokenBody(parsedToken);
        }catch (Exception e) {
            if (!(e instanceof ExpiredJwtException || e instanceof PrematureJwtException)) throw new JwtException("Invalid jwt");
        }
        return parsedToken;
    }

    private void validateTokenBody(Jws<Claims> claims) {
        if (claims != null &&
                !claims.getBody().getAudience().equals(CREDENTIALS.jwtAudience) &&
                !claims.getBody().getIssuer().equals(CREDENTIALS.jwtIssuer))
            throw new JwtException("Invalid JWT");
    }

    protected String extractUsernameFromExpiredAccessToken(String accessToken) {
        JwtParser parser = createParser(CREDENTIALS.jwtSecret);
        String username;
        try {
            username = parser.parseClaimsJws(accessToken).getBody().getSubject();
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) username = ((ExpiredJwtException) e).getClaims().getSubject();
            else throw new JwtException("Invalid token, please login");
        }
        return username;
    }
}

