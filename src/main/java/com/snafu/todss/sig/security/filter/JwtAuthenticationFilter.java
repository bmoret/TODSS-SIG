package com.snafu.todss.sig.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snafu.todss.sig.security.application.util.JwtGenerator;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.presentation.dto.request.Login;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtGenerator JWT_GENERATOR;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(
            String path,
            JwtGenerator jwtGenerator,
            AuthenticationManager authenticationManager
    ) {
        super(new AntPathRequestMatcher(path));
        this.JWT_GENERATOR = jwtGenerator;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        Login login = new ObjectMapper()
                .readValue(request.getInputStream(), Login.class);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.username, login.password)
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String accessToken = JWT_GENERATOR.generateAccessToken(user);
        String refreshToken = JWT_GENERATOR.generateRefreshToken();

        response.addHeader("Access-Token", "Bearer " + accessToken);
        response.addHeader("Refresh-Token", refreshToken);
        response.addHeader("User-Username", user.getUsername());
        response.addHeader("User-Role", user.getRole().toString());
        response.addHeader("User-Id", user.getId().toString());
        response.addHeader("Person-Id", user.getPerson().getId().toString());
        response.addHeader("Access-Control-Expose-Headers", "*");
    }
}
