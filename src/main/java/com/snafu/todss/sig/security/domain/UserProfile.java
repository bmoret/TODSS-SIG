package com.snafu.todss.sig.security.domain;

public class UserProfile extends User{
    private final String username;

    public UserProfile(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
