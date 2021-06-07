package com.snafu.todss.sig.security.application.util;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.domain.User;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtGenerator {
    private final JwtCredentials CREDENTIALS;
    private final UserService userService;

    public JwtGenerator(JwtCredentials jwtCredentials, @Lazy UserService userService) {
        this.CREDENTIALS = jwtCredentials;
        this.userService = userService;
    }

    public String generateAccessToken(User user) {
        return createAccessToken(user);
    }

    public String refreshAccessTokenFromAccessToken(String accessToken) {
        String username = new JwtValidator(CREDENTIALS).extractUsernameFromExpiredAccessToken(accessToken);
        User user = userService.loadUserByUsername(username);
        return createAccessToken(user);
    }

    public String generateRefreshToken() {
        return createRefreshToken();
    }

    private String createAccessToken(User user) {
        JwtBuilder builder = createBuilderSetup();
        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return builder.signWith(Keys.hmacShaKeyFor(CREDENTIALS.jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + CREDENTIALS.jwtExpirationInMs))
                .setSubject(user.getUsername())
                .claim("role", roles.toArray())
                .compact();
    }

    private String createRefreshToken() {
        JwtBuilder builder = createBuilderSetup();
        return builder.signWith(Keys.hmacShaKeyFor(CREDENTIALS.jwtRefreshSecret.getBytes()), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + CREDENTIALS.jwtRefreshExpirationInMs))
                .compact();
    }

    private JwtBuilder createBuilderSetup(){
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setIssuer(CREDENTIALS.jwtIssuer)
                .setAudience(CREDENTIALS.jwtAudience);
    }

}
