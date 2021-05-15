package com.snafu.todss.sig.security.presentation.dto.request;

import com.snafu.todss.sig.security.domain.User;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

public class UserDTOResponse extends RepresentationModel<UserDTOResponse> {
    private UUID id;
    private String username;
    private String password;

    public UserDTOResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
